package invest.automate.trade.marketdata.websocket;

import invest.automate.trade.model.Tick;
import invest.automate.trade.strategy.StrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simulates Zerodha WebSocket for receiving tick data.
 * In production, replace with KiteConnect WebSocket streaming.
 */
@Slf4j
@RequiredArgsConstructor
public class ZerodhaWebSocketClient {

    private final StrategyManager strategyManager;
    private final Random random = new Random();

    public void connect() {
        log.info("Connecting to mock Zerodha WebSocket...");

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Tick tick = Tick.builder()
                        .instrumentToken("RELIANCE")
                        .lastTradedPrice(2500 + random.nextGaussian() * 10)
                        .timestamp(System.currentTimeMillis())
                        .build();
                strategyManager.onTick(tick);
            }
        }, 1000, 1000);
    }
}
