package invest.automate.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "trading.profile.live")
@Data
public class LiveConfig {

    private int defaultOrderQuantity = 1;     // e.g. 1 (number of contracts/shares per trade)
    private String tradeProduct = "MIS";      // e.g. MIS, CNC, NRML
    private String tradeExchange = "NSE";     // e.g. NSE, BSE

}
