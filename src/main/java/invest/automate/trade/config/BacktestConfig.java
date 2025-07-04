package invest.automate.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "trading.profile.backtest")
@Data
public class BacktestConfig {

    private String provider;                // Ex: json, csv, api, etc.
    private String jsonPath;                // e.g. src/main/resources/sample_ticks.json
    private String csvPath;                 // e.g. src/main/resources/sample_ticks.csv
    private String apiInstrumentToken;
    private String apiInterval;
    private String apiFrom;
    private String apiTo;

}
