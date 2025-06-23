package invest.automate.trade.strategy;

import invest.automate.trade.model.Tick;
import invest.automate.trade.strategy.base.Strategy;
import invest.automate.trade.strategy.base.StrategyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the lifecycle and invocation of trading strategies.
 */
@Slf4j
public class StrategyManager {
    private final Map<String, Strategy> activeStrategies = new HashMap<>();

    public void registerStrategy(String strategyId, Strategy strategy, StrategyConfig config) {
        strategy.initialize(config);
        activeStrategies.put(strategyId, strategy);
        log.info("Registered strategy: {}", strategyId);
    }

    public void onTick(Tick tick) {
        for (Strategy strategy : activeStrategies.values()) {
            strategy.onTick(tick);
        }
    }

    public void removeStrategy(String strategyId) {
        activeStrategies.remove(strategyId);
        log.info("Removed strategy: {}", strategyId);
    }

    public void clearAll() {
        activeStrategies.clear();
        log.info("Cleared all strategies");
    }
}
