package invest.automate.trade.service;

import com.zerodhatech.models.Tick;
import invest.automate.trade.config.IndicatorConfig;
import invest.automate.trade.indicator.IndicatorRegistryService;
import invest.automate.trade.ml.MlModelService;
import invest.automate.trade.ml.utils.WekaInstanceBuilder;
import invest.automate.trade.service.broker.OrderExecutorService;
import invest.automate.trade.service.broker.WebSocketTickService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import weka.core.Instance;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveTradeService {

    private final IndicatorConfig indicatorConfig;

    private final MlModelService mlModelService;
    private final IndicatorRegistryService indicatorRegistryService;
    private final SignalCompositionService signalCompositionService;
    private final WebSocketTickService wsService;
    private final SeriesManagerService seriesManager;
    private final OrderExecutorService orderExecutorService;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public void startLiveTrading(Object request) {
        if (running.get()) {
            log.warn("Live trading already running.");
            return;
        }
        running.set(true);

        wsService.addTickListener(ticks -> {
            if (!running.get()) {
                return;
            }

            for (Tick tick : ticks) {
                for (int barDuration : indicatorConfig.getBarDurations()) {
                    BarSeries series = seriesManager.getSeries(tick.getInstrumentToken(), barDuration);
                    if (series == null || series.getBarCount() < 2) {
                        continue;
                    }
                    Bar bar = series.getLastBar();
                    Map<String, Double> indicators = indicatorRegistryService.getAllIndicatorValues(series,
                            series.getEndIndex());
                    var pair = WekaInstanceBuilder.buildInstance(tick, bar, indicators, null);
                    Instance inst = pair.second;

                    SignalCompositionService.Signal signal;
                    try {
                        signal = signalCompositionService.composeSignal(series, series.getEndIndex(), inst);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    if (signal == SignalCompositionService.Signal.BUY) {
                        orderExecutorService.executeOrder("BUY", tick.getLastTradedPrice(),
                                tick.getInstrumentToken(), true);
                    } else if (signal == SignalCompositionService.Signal.SELL) {
                        orderExecutorService.executeOrder("SELL", tick.getLastTradedPrice(),
                                tick.getInstrumentToken(), true);
                    }
                }
            }
        });
        wsService.startWebSocket();
        log.info("Live trading started.");
    }

    public void stopLiveTrading() {
        running.set(false);
        wsService.stopWebSocket();
        log.info("Live trading stopped.");
    }
}
