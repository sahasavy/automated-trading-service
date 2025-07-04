package invest.automate.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ml")
@Data
public class MlConfig {

    private String modelName;                 // e.g. "RandomForest", "J48", etc.
}
