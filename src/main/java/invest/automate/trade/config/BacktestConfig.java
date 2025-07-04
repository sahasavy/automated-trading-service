package invest.automate.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "trading.profile.backtest")
@Data
public class BacktestConfig {

    private String fileType;                // Ex: json, csv, etc.
    private String historicFilePath;          // e.g. src/main/resources/sample_ticks.json

}
