package invest.automate.trade.service;

import com.zerodhatech.models.Tick;
import invest.automate.trade.config.TradingConfig;
import invest.automate.trade.service.broker.OrderExecutorService;
import invest.automate.trade.service.broker.WebSocketTickService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveTradeService {

    private final TradingConfig config;
    private final WebSocketTickService wsService;
    private final SeriesManagerService seriesManager;
    private final SignalCompositionService signalCompositionService;
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
                seriesManager.onTicks(List.of(tick));

                for (int barDuration : config.getBarDurations()) {
                    BarSeries series = seriesManager.getSeries(tick.getInstrumentToken(), barDuration);
                    SignalCompositionService.Signal signal = signalCompositionService.compositeSignal(series);

                    if (signal == SignalCompositionService.Signal.BUY) {
                        orderExecutorService.executeOrder("BUY", tick.getLastTradedPrice(),
                                tick.getInstrumentToken(), true);
                    }
                    if (signal == SignalCompositionService.Signal.SELL) {
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
