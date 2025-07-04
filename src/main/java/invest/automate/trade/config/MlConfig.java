package invest.automate.trade.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ml")
@Data
public class MlConfig {

    @Value("${model-name}")
    private String model;                 // e.g. "RandomForest", "J48", etc.
}
