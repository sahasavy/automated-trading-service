package invest.automate.trade.backtest;

import com.zerodhatech.models.Tick;
import invest.automate.trade.config.BacktestConfig;
import invest.automate.trade.config.IndicatorConfig;
import invest.automate.trade.indicator.IndicatorRegistryService;
import invest.automate.trade.ml.MlModelService;
import invest.automate.trade.ml.utils.WekaInstanceBuilder;
import invest.automate.trade.service.SeriesManagerService;
import invest.automate.trade.service.SignalCompositionService;
import invest.automate.trade.backtest.provider.HistoricDataProvider;
import invest.automate.trade.backtest.provider.JsonFileHistoricDataProvider;
import invest.automate.trade.backtest.provider.CsvFileHistoricDataProvider;
import invest.automate.trade.backtest.provider.ApiHistoricDataProvider;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BacktestService {

    private final BacktestConfig backtestConfig;
    private final IndicatorConfig indicatorConfig;

    private final IndicatorRegistryService indicatorRegistryService;
    private final MlModelService mlModelService;
    private final SignalCompositionService signalCompositionService;
    private final SeriesManagerService seriesManagerService;

    private final JsonFileHistoricDataProvider jsonProvider;
    private final CsvFileHistoricDataProvider csvProvider;
    private final ApiHistoricDataProvider apiProvider;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TradeLog {
        private int barIndex;
        private String datetime;
        private String side;
        private double price;
        private double pnlAfterTrade;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BacktestResult {
        private String summary;
        private int trades;
        private double totalPnL;
        private double winRate;
        private List<TradeLog> tradeLogs;
    }

    public BacktestResult runBacktest() throws Exception {
        HistoricDataProvider historicDataProvider = switch (backtestConfig.getProvider().toLowerCase()) {
            case "json" -> jsonProvider;
            case "csv" -> csvProvider;
            case "api" -> apiProvider;
            default -> throw new IllegalStateException("Unexpected Provider value: " +
                    backtestConfig.getProvider().toLowerCase());
        };

        List<Tick> ticks = historicDataProvider.loadHistoricTicks();
        if (ticks == null || ticks.isEmpty()) {
            log.warn("No historic ticks loaded!");
            return new BacktestResult("No ticks loaded.", 0, 0, 0,
                    Collections.emptyList());
        }

        // ---- Use SeriesManagerService to build all bar series ----
        seriesManagerService.onTicks(ticks);

        // For simplicity, backtest only on the first configured instrument and duration:
        long mainToken = ticks.getFirst().getInstrumentToken();
        int mainDuration = indicatorConfig.getBarDurations().getFirst();

        BarSeries series = seriesManagerService.getSeries(mainToken, mainDuration);

        if (series == null || series.getBarCount() < 20) {
            log.warn("Bar series too short for backtest.");
            return new BacktestResult("Bar series too short.", 0, 0, 0,
                    Collections.emptyList());
        }

        // ---- ML training as before ----
        List<String> classLabels = List.of("UP", "DOWN", "NONE");
        List<Instance> trainingInstances = new ArrayList<>();
        for (int i = 1; i < series.getBarCount() / 2; i++) {
            Bar bar = series.getBar(i);
            Map<String, Double> indicators = indicatorRegistryService.getAllIndicatorValues(series, i);
            var pair = WekaInstanceBuilder.buildInstance(null, bar, indicators, classLabels);
            Instance inst = pair.second;
            double nextClose = series.getBar(i + 1).getClosePrice().doubleValue();
            double thisClose = bar.getClosePrice().doubleValue();
            inst.setClassValue(nextClose > thisClose ? "UP" : "DOWN");
            trainingInstances.add(inst);
        }
        if (!trainingInstances.isEmpty()) {
            Instances trainingSet = new Instances(trainingInstances.getFirst().dataset());
            trainingSet.addAll(trainingInstances);
            mlModelService.trainAll(trainingSet);
        }

        // ---- Backtest loop ----
        boolean holding = false;
        String lastSide = null;
        double entryPrice = 0;
        int winTrades = 0;
        int totalTrades = 0;
        double totalPnL = 0;
        List<TradeLog> tradeLogs = new ArrayList<>();

        for (int i = series.getBarCount() / 2; i < series.getBarCount() - 1; i++) {
            Bar bar = series.getBar(i);
            Map<String, Double> indicators = indicatorRegistryService.getAllIndicatorValues(series, i);
            var pair = WekaInstanceBuilder.buildInstance(null, bar, indicators, null);
            Instance inst = pair.second;

            SignalCompositionService.Signal signal = signalCompositionService.composeSignal(series, i, inst);

            if (!holding && signal == SignalCompositionService.Signal.BUY) {
                holding = true;
                lastSide = "BUY";
                entryPrice = bar.getClosePrice().doubleValue();
                tradeLogs.add(new TradeLog(i, bar.getEndTime().toString(), "BUY", entryPrice, totalPnL));
                totalTrades++;
            } else if (holding && signal == SignalCompositionService.Signal.SELL) {
                holding = false;
                double exitPrice = bar.getClosePrice().doubleValue();
                double tradePnL = exitPrice - entryPrice;
                totalPnL += tradePnL;
                boolean win = tradePnL > 0;
                if (win) winTrades++;
                tradeLogs.add(new TradeLog(i, bar.getEndTime().toString(), "SELL", exitPrice, totalPnL));
                totalTrades++;
            }
        }
        double winRate = totalTrades > 0 ? (100.0 * winTrades / totalTrades) : 0;
        String summary = String.format("Backtest complete: Trades=%d, WinRate=%.2f%%, TotalPnL=%.2f",
                totalTrades, winRate, totalPnL);

        return new BacktestResult(summary, totalTrades, totalPnL, winRate, tradeLogs);
    }
}
