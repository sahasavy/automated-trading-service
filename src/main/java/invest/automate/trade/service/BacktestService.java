package invest.automate.trade.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerodhatech.models.Tick;
import invest.automate.trade.config.TradingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BacktestService {

    private final TradingConfig config;
    private final SeriesManagerService seriesManager;
    private final IndicatorService indicatorService;
    private final WekaModelService wekaModelService;

    public BacktestResult runBacktest(Object request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File(config.getHistoricFilePath());
            List<Tick> ticks = mapper.readValue(jsonFile, new TypeReference<List<Tick>>() {
            });
            int trainLimit = ticks.size() * 2 / 3;

            // 1. TRAIN ML model on first 2/3 of data
            for (int i = 0; i < trainLimit; i++)
                seriesManager.onTicks(List.of(ticks.get(i)));
            BarSeries barSeries = seriesManager.getSeries(ticks.get(0).instrumentToken, config.getBarDurations().get(0));
            if (barSeries.getBarCount() > 20)
                wekaModelService.trainModel(barSeries);

            // 2. TEST signals + ML prediction on rest
            int buySignals = 0, mlUp = 0;
            double pnl = 0;
            for (int i = trainLimit; i < ticks.size(); i++) {
                Tick tick = ticks.get(i);
                seriesManager.onTicks(List.of(tick));
                BarSeries series = seriesManager.getSeries(tick.instrumentToken, config.getBarDurations().get(0));
                if (series.getBarCount() < 21) continue;
                var ta4jSignal = indicatorService.evaluateEmaCrossover(series);
                String mlSignal = "NONE";
                if (wekaModelService != null && barSeries.getBarCount() > 15)
                    mlSignal = wekaModelService.predict(series);
                if (ta4jSignal == IndicatorService.Signal.BUY) buySignals++;
                if ("UP".equals(mlSignal)) mlUp++;
                // Example: count as correct if ML/indicator says "UP" and next tick price goes up
                if (i + 1 < ticks.size() && ("UP".equals(mlSignal) || ta4jSignal == IndicatorService.Signal.BUY)) {
                    double cur = tick.lastTradedPrice;
                    double next = ticks.get(i + 1).lastTradedPrice;
                    pnl += (next - cur);
                }
            }
            String summary = "Backtest: TA4J Buys=" + buySignals + ", ML UPs=" + mlUp + ", Simulated P&L=" + pnl;
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
