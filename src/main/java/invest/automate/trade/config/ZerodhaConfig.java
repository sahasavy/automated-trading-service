package invest.automate.trade.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zerodha")
@Data
public class ZerodhaConfig {

    @Value("${account.user-id}")
    private String zerodhaAccountUserId;

    @Value("${kite.api-key}")
    private String zerodhaKiteApiKey;

    @Value("${kite.api-secret}")
    private String zerodhaKiteApiSecret;

    @Value("${kite.request-token}")
    private String zerodhaKiteRequestToken;

    @Value("${kite.access-token}")
    private String zerodhaKiteAccessToken;

    @Value("${kite.public-token}")
    private String zerodhaKitePublicToken;

    @Value("${ticker.try-reconnection}")
    private boolean zerodhaTickerTryReconnection;

    @Value("${ticker.max-retries}")
    private int zerodhaTickerMaxRetries;

    @Value("${ticker.max-retry-interval-sec}")
    private int zerodhaTickerMaxRetryIntervalSec;
}
