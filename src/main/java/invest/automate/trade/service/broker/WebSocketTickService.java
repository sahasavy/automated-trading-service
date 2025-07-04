package invest.automate.trade.service.broker;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.*;
import invest.automate.trade.config.TradingConfig;
import invest.automate.trade.config.ZerodhaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketTickService {

    private final ZerodhaKiteService kiteService;
    private final TradingConfig tradingConfig;
    private final ZerodhaConfig zerodhaConfig;

    private KiteTicker kiteTicker;
    private final List<Consumer<List<Tick>>> listeners = new ArrayList<>();

    public void addTickListener(Consumer<List<Tick>> listener) {
        listeners.add(listener);
    }

    private void initializeWebSocket() {
        KiteConnect kiteConnect = kiteService.getKiteConnect();
        String apiKey = kiteConnect.getApiKey();
        String accessToken = kiteConnect.getAccessToken();
        kiteTicker = new KiteTicker(accessToken, apiKey);

        ArrayList<Long> instrumentTokens = new ArrayList<>(tradingConfig.getInstrumentTokens());

        kiteTicker.setOnConnectedListener(handleOnConnected(instrumentTokens));
        kiteTicker.setOnDisconnectedListener(handleOnDisconnected());
        kiteTicker.setOnTickerArrivalListener(handleOnTickerArrival());
        kiteTicker.setOnOrderUpdateListener(handleOnOrderUpdate());
        kiteTicker.setOnErrorListener(handleErrors());

        try {
            kiteTicker.setTryReconnection(zerodhaConfig.isTickerTryReconnection());            // Call before calling connect().
            kiteTicker.setMaximumRetries(zerodhaConfig.getTickerMaxRetries());                 // Should be greater than 0
            kiteTicker.setMaximumRetryInterval(zerodhaConfig.getTickerMaxRetryIntervalSec());  // Unit is seconds
        } catch (KiteException e) {
            log.error("Kite Ticker Exception occurred", e);
            throw new RuntimeException(e);
        }
    }

    public void startWebSocket() {
        initializeWebSocket();

        log.info("Connecting to Kite WebSocket...");
        try {
            kiteTicker.connect();
        } catch (Exception ex) {
            log.error("Exception occurred while connecting to the Kite WebSocket : ", ex);
        }

        boolean isConnected = kiteTicker.isConnectionOpen();
        log.info("Websocket connection open: {}", isConnected);
    }

    private OnTicks handleOnTickerArrival() {
        return ticks -> {
            log.info("Ticks size {}", ticks.size());

            for (Consumer<List<Tick>> listener : listeners) {
                listener.accept(ticks);
            }
        };
    }

    private OnConnect handleOnConnected(ArrayList<Long> instrumentTokens) {
        return () -> {
            log.info("KiteTicker connected, subscribing tokens {}", instrumentTokens);
            kiteTicker.subscribe(instrumentTokens);
            kiteTicker.setMode(instrumentTokens, KiteTicker.modeFull);
        };
    }

    private OnDisconnect handleOnDisconnected() {
        return () -> {
            log.warn("KiteTicker disconnected!");
            // TODO - Implement reconnect logic here
        };
    }

    private OnOrderUpdate handleOnOrderUpdate() {
        return order -> {
            // TODO - Implement logic here
            log.info("Received Order update for orderId {}", order.orderId);
            log.info("Received Order update for tradingSymbol {}", order.orderId);
        };
    }

    private OnError handleErrors() {
        return new OnError() {
            @Override
            public void onError(Exception e) {
                log.error("Exception occurred : ");
                //handle here.
            }

            @Override
            public void onError(KiteException e) {
                log.error("KiteException occurred : ");
                //handle here.
            }

            @Override
            public void onError(String error) {
                log.error("String error occurred : {}", error);
                //handle here.
            }
        };
    }

    public void stopWebSocket() {
        if (nonNull(kiteTicker) && kiteTicker.isConnectionOpen()) {
            kiteTicker.disconnect();
        } else {
            log.error("stopWebSocket: Kite Ticker object is null or ConnectionOpen flag is false");
        }
    }

    public void subscribeTokens(ArrayList<Long> instrumentTokens) {
        if (nonNull(kiteTicker) && kiteTicker.isConnectionOpen()) {
            kiteTicker.subscribe(instrumentTokens);
        } else {
            log.error("subscribeTokens: Kite Ticker object is null or ConnectionOpen flag is false");
        }
    }

    public void unsubscribeTokens(ArrayList<Long> instrumentTokens) {
        if (nonNull(kiteTicker) && kiteTicker.isConnectionOpen()) {
            kiteTicker.unsubscribe(instrumentTokens);
        } else {
            log.error("unsubscribeTokens: Kite Ticker object is null or ConnectionOpen flag is false");
        }
    }

    /**
     * Ticker allows three modes:
     * 1. modeFull: For getting all data with depth
     * 2. modeQuote: For getting last traded price, last traded quantity, average price, volume traded today,
     * total sell quantity and total buy quantity, open, high, low, close, change
     * 3. modeLTP: For getting only last traded price
     */
    private void setMode(ArrayList<Long> instrumentTokens, String mode) {
        if (nonNull(kiteTicker) && kiteTicker.isConnectionOpen()) {
            kiteTicker.setMode(instrumentTokens, KiteTicker.modeLTP);
        } else {
            log.error("setMode: Kite Ticker object is null or ConnectionOpen flag is false");
        }
    }
}
