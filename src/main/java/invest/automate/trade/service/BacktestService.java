package invest.automate.trade.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerodhatech.models.Tick;
import invest.automate.trade.config.TradingConfig;
import invest.automate.trade.service.indicator.IndicatorService;
import invest.automate.trade.service.ml.MlModelService;
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
    private final MlModelService mlModelService;

    public BacktestResult runBacktest(Object request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File(config.getHistoricFilePath());
            List<Tick> ticks = mapper.readValue(jsonFile, new TypeReference<>() {
            });
            int trainLimit = ticks.size() * 2 / 3;

            // 1. TRAIN ML model on first 2/3 of data
            for (int i = 0; i < trainLimit; i++) {
                seriesManager.onTicks(List.of(ticks.get(i)));
            }
            BarSeries barSeries = seriesManager.getSeries(ticks.getFirst().getInstrumentToken(),
                    config.getBarDurations().getFirst());
            if (barSeries.getBarCount() > 20) {
                mlModelService.trainModel(barSeries);
            }

            // 2. TEST signals + ML prediction on remaining 1/3 data
            int buySignals = 0, mlUp = 0;
            double pnl = 0;
            for (int limit = trainLimit; limit < ticks.size(); limit++) {
                Tick tick = ticks.get(limit);
                seriesManager.onTicks(List.of(tick));
                BarSeries series = seriesManager.getSeries(tick.getInstrumentToken(),
                        config.getBarDurations().getFirst());
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
