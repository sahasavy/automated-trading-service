package invest.automate.trade.strategy.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Wrapper for passing configuration parameters to a strategy.
 */
@Data
@Builder
@AllArgsConstructor
public class StrategyConfig {
    private Map<String, Object> parameters;
}
