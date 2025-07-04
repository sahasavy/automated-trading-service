package invest.automate.trade.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnError;
import invest.automate.trade.config.TradingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketTickService {

    private final ZerodhaKiteService kiteService;
    private final TradingConfig config;

    private KiteTicker kiteTicker;
    private final List<Consumer<List<Tick>>> listeners = new ArrayList<>();

    public void addTickListener(Consumer<List<Tick>> listener) {
        listeners.add(listener);
    }

    public void startWebSocket() {
        KiteConnect kiteConnect = kiteService.getKiteConnect();
        String apiKey = config.getApiKey();
        String accessToken = kiteConnect.getAccessToken();
        ArrayList<Long> tokens = new ArrayList<>(config.getInstrumentTokens());

        kiteTicker = new KiteTicker(accessToken, apiKey);

        kiteTicker.setOnConnectedListener(() -> {
            log.info("KiteTicker connected, subscribing tokens {}", tokens);
            kiteTicker.subscribe(tokens);
            kiteTicker.setMode(tokens, KiteTicker.modeFull);
        });

        kiteTicker.setOnTickerArrivalListener(ticks -> {
            for (Consumer<List<Tick>> listener : listeners) {
                listener.accept(ticks);
            }
        });

        kiteTicker.setOnDisconnectedListener(() ->
                log.warn("KiteTicker disconnected!")
        );

        kiteTicker.setOnErrorListener(handleErrors());

        log.info("Connecting to Kite WebSocket...");
        kiteTicker.connect();
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
        if (kiteTicker != null) {
            kiteTicker.disconnect();
        }
    }
}
