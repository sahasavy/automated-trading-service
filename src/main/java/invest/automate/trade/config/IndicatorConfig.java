package invest.automate.trade.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "indicator")
@Data
public class IndicatorConfig {

    @Value("${bar-durations}")
    private List<Integer> barDurations;       // e.g. [1, 3, 5] in seconds
}
