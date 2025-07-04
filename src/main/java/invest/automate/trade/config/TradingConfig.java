package invest.automate.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "trading")
@Data
public class TradingConfig {
    private String apiKey;
    private String apiSecret;
    private String accessToken;

    private ArrayList<Long> instrumentTokens;      // e.g. [256265, 738561]
    private List<Integer> barDurations;       // e.g. [1, 3, 5] in seconds
    private String wekaModel;                 // e.g. "RandomForest", "J48", etc.
    private String historicFilePath;          // e.g. src/main/resources/2024-11-19.json

    private int defaultOrderQuantity = 1;     // e.g. 1 (number of contracts/shares per trade)
    private String tradeProduct = "MIS";      // e.g. MIS, CNC, NRML
    private String tradeExchange = "NSE";     // e.g. NSE, BSE
    private boolean paperTrade = true;        // true for paper trading, false for live

}
