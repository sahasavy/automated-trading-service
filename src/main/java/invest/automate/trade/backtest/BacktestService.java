package invest.automate.trade.backtest;

import com.zerodhatech.models.Tick;
import invest.automate.trade.backtest.provider.ApiHistoricDataProvider;
import invest.automate.trade.backtest.provider.CsvFileHistoricDataProvider;
import invest.automate.trade.backtest.provider.HistoricDataProvider;
import invest.automate.trade.backtest.provider.JsonFileHistoricDataProvider;
import invest.automate.trade.config.BacktestConfig;
import invest.automate.trade.config.IndicatorConfig;
import invest.automate.trade.service.SeriesManagerService;
import invest.automate.trade.service.indicator.IndicatorService;
import invest.automate.trade.service.ml.MlModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BacktestService {

    private final JsonFileHistoricDataProvider jsonProvider;
    private final CsvFileHistoricDataProvider csvProvider;
    private final ApiHistoricDataProvider apiProvider;

    private final BacktestConfig backtestConfig;
    private final IndicatorConfig indicatorConfig;

    private final SeriesManagerService seriesManagerService;
    private final IndicatorService indicatorService;
    private final MlModelService mlModelService;

    public BacktestResult runBacktest() {
        try {
            HistoricDataProvider historicDataProvider = switch (backtestConfig.getProvider().toLowerCase()) {
                case "json" -> jsonProvider;
                case "csv" -> csvProvider;
                case "api" -> apiProvider;
                default -> throw new IllegalStateException("Unexpected Provider value: " +
                        backtestConfig.getProvider().toLowerCase());
            };

            List<Tick> ticks = historicDataProvider.loadHistoricTicks();
            int trainLimit = ticks.size() * 2 / 3;

            // 1. TRAIN ML model on first 2/3 of data
            for (int i = 0; i < trainLimit; i++) {
                seriesManagerService.onTicks(List.of(ticks.get(i)));
            }
            BarSeries barSeries = seriesManagerService.getSeries(ticks.getFirst().getInstrumentToken(),
                    indicatorConfig.getBarDurations().getFirst());
            if (barSeries.getBarCount() > 20) {
                mlModelService.trainModel(barSeries);
            }

            // 2. TEST signals + ML prediction on remaining 1/3 data
            int buySignals = 0, mlUp = 0;
            double pnl = 0;
            for (int limit = trainLimit; limit < ticks.size(); limit++) {
                Tick tick = ticks.get(limit);
                seriesManagerService.onTicks(List.of(tick));
                BarSeries series = seriesManagerService.getSeries(tick.getInstrumentToken(),
                        indicatorConfig.getBarDurations().getFirst());
                if (series.getBarCount() < 21) {
                    continue;
                }

                IndicatorService.Signal indicatorSignal = indicatorService.evaluateEmaCrossover(series);
                String mlSignal = "NONE";
                if (mlModelService != null && barSeries.getBarCount() > 15) {
                    mlSignal = mlModelService.predict(series);
                }

                if (indicatorSignal == IndicatorService.Signal.BUY) {
                    buySignals++;
                }

                if ("UP".equals(mlSignal)) {
                    mlUp++;
                }

                // Example: count as correct if ML/indicator says "UP" and next tick price goes up
                if (limit + 1 < ticks.size() && ("UP".equals(mlSignal) ||
                        indicatorSignal == IndicatorService.Signal.BUY)) {
                    double cur = tick.getLastTradedPrice();
                    double next = ticks.get(limit + 1).getLastTradedPrice();
                    pnl += (next - cur);
                }
            }
            String summary = "Backtest: INDICATOR Buys=" + buySignals + ", ML UPs=" + mlUp + ", Simulated P&L=" + pnl;
            log.info(summary);
            return new BacktestResult(summary);
        } catch (Exception e) {
            log.error("Backtest error: {}", e.getMessage(), e);
            return new BacktestResult("Backtest failed: " + e.getMessage());
        }
    }

    public static class BacktestResult {
        private final String summary;

        public BacktestResult(String summary) {
            this.summary = summary;
        }

        public String getSummary() {
            return summary;
        }
    }
}
