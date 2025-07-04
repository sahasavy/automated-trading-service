package invest.automate.trade.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import invest.automate.trade.config.TradingConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZerodhaKiteService {

    private final TradingConfig config;

    @Getter
    private KiteConnect kiteConnect;

    @PostConstruct
    public void init() {
        kiteConnect = new KiteConnect(config.getApiKey());
        // Interactive login required to generate requestToken and accessToken:
        // See Zerodha docs: https://kite.trade/docs/connect/v3/user/#login-flow
        // For production, you may automate this using a headless browser or manual prompt.
        try {
            log.info("KiteConnect API loaded. Please ensure accessToken is set in config or via login flow.");
            // You can set accessToken if available:
            // kiteConnect.setAccessToken("your_access_token");
        } catch (Exception e) {
            log.error("Failed to initialize KiteConnect: {}", e.getMessage(), e);
        }
    }
}
