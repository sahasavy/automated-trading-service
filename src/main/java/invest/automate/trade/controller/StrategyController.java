package invest.automate.trade.controller;

import invest.automate.trade.strategy.StrategyManager;
import invest.automate.trade.strategy.base.Strategy;
import invest.automate.trade.strategy.base.StrategyConfig;
import invest.automate.trade.strategy.impl.MovingAverageCrossoverStrategy;
import invest.automate.trade.strategy.impl.MeanReversionStrategy;
import invest.automate.trade.strategy.impl.MomentumStrategy;
import invest.automate.trade.signal.SignalDispatcher;
import invest.automate.trade.execution.order.OrderExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for managing trading strategies.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/strategies")
public class StrategyController {

    private final StrategyManager strategyManager;
    private final OrderExecutor orderExecutor;

    @PostMapping("/moving-average")
    public String registerMovingAvgStrategy(@RequestBody Map<String, Object> config) {
        Strategy strategy = new MovingAverageCrossoverStrategy();
        return register("movingAvg", strategy, config);
    }

    @PostMapping("/mean-reversion")
    public String registerMeanReversion(@RequestBody Map<String, Object> config) {
        Strategy strategy = new MeanReversionStrategy();
        return register("meanRevert", strategy, config);
    }

    @PostMapping("/momentum")
    public String registerMomentum(@RequestBody Map<String, Object> config) {
        Strategy strategy = new MomentumStrategy();
        return register("momentum", strategy, config);
    }

    private String register(String id, Strategy strategy, Map<String, Object> config) {
        config.put("dispatcher", (SignalDispatcher) orderExecutor::execute);
        strategyManager.registerStrategy(id, strategy, StrategyConfig.builder().parameters(config).build());
        return "Registered strategy: " + id;
    }

    @DeleteMapping("/{id}")
    public String removeStrategy(@PathVariable String id) {
        strategyManager.removeStrategy(id);
        return "Removed strategy: " + id;
    }
}
