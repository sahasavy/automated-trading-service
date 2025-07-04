package invest.automate.trade.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zerodha")
@Data
public class ZerodhaConfig {

    private String accountUserId;

    private String kiteApiKey;
    private String kiteApiSecret;
    private String kiteRequestToken;
    private String kiteAccessToken;
    private String kitePublicToken;

    private boolean tickerTryReconnection;
    private int tickerMaxRetries;
    private int tickerMaxRetryIntervalSec;

}
