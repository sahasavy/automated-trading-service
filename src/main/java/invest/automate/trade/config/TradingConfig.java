package invest.automate.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@ConfigurationProperties(prefix = "trading")
@Data
public class TradingConfig {

    private ArrayList<Long> instrumentTokens;      // e.g. [256265, 738561]

}
