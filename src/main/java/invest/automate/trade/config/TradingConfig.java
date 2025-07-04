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
    private String historicFilePath;          // e.g. src/main/resources/sample_ticks.json

    private int defaultOrderQuantity = 1;     // e.g. 1 (number of contracts/shares per trade)
    private String tradeProduct = "MIS";      // e.g. MIS, CNC, NRML
    private String tradeExchange = "NSE";     // e.g. NSE, BSE
    private boolean paperTrade = true;        // true for paper trading, false for live

}
