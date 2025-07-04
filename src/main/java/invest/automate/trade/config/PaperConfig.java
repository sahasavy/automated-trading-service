package invest.automate.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "trading.profile.paper")
@Data
public class PaperConfig {

    private boolean paperTrade = true;        // true for paper trading, false for live

}
