package invest.automate.trade.marketdata.websocket;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.ticker.OnDisconnect;
import com.zerodhatech.ticker.OnTicks;
import com.zerodhatech.models.Tick;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class ZerodhaWebSocketClient {

    private String apiKey;
    private String accessToken;
    private String userId;
    private KiteTicker kiteTicker;

    public ZerodhaWebSocketClient() {
        // Read credentials from environment variables for security
        this.apiKey = System.getenv("KITE_API_KEY");
        this.accessToken = System.getenv("KITE_ACCESS_TOKEN");
        this.userId = System.getenv("KITE_USER_ID");  // optional: user ID if needed
        if (apiKey == null || accessToken == null) {
            throw new IllegalStateException("Kite API credentials not set in environment variables.");
        }
    }

    /**
     * Connects to the Zerodha WebSocket and subscribes to given instrument tokens.
     * @param instrumentTokens List of instrument tokens to subscribe for tick data.
     */
    public void connectAndSubscribe(ArrayList<Long> instrumentTokens) {
        // Initialize KiteConnect and KiteTicker
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setAccessToken(accessToken);
        if (userId != null) {
            kiteConnect.setUserId(userId);
        }

        // Create KiteTicker for live market data
        kiteTicker = new KiteTicker(accessToken, apiKey);

        // Set up event listeners
        kiteTicker.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                log.info("WebSocket connected, subscribing to tokens: {}", instrumentTokens);
                // Subscribe to given tokens and set mode to full to get all tick fields
                kiteTicker.subscribe(instrumentTokens);
                kiteTicker.setMode(instrumentTokens, KiteTicker.modeFull);
            }
        });

        kiteTicker.setOnDisconnectedListener(new OnDisconnect() {
            @Override
            public void onDisconnected() {
                log.warn("WebSocket disconnected.");
            }
        });

        kiteTicker.setOnTickerArrivalListener(new OnTicks() {
            @Override
            public void onTicks(ArrayList<Tick> ticks) {
                // This method is called whenever new tick data arrives
                for (Tick tick : ticks) {
                    long token = tick.getInstrumentToken();
                    double ltp = tick.getLastTradedPrice();
                    log.info("Tick received -> Token: {}, Last Traded Price: {}", token, ltp);
                }
            }
        });

        // Establish the connection
        log.info("Connecting to Kite ticker...");
        kiteTicker.connect();
    }

    /**
     * Disconnects the WebSocket connection gracefully.
     */
    public void disconnect() {
        if (kiteTicker != null) {
            kiteTicker.disconnect();
            log.info("WebSocket connection closed.");
        }
    }

    // Simple main method for standalone testing of tick data streaming
    public static void main(String[] args) throws InterruptedException {
        ZerodhaWebSocketClient client = new ZerodhaWebSocketClient();
        // Example instrument tokens list (replace with actual tokens you want to subscribe)
        ArrayList<Long> tokens = new ArrayList<>();
        tokens.add(256265L);  // e.g., token for NIFTY 50 index (for demo purposes)
        client.connectAndSubscribe(tokens);

        // Keep the main thread alive to continue receiving ticks (demo: run for 30 seconds)
        Thread.sleep(30000);
        client.disconnect();
    }
}
