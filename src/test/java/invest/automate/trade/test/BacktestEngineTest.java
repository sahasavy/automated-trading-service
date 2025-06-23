package invest.automate.trade.test;

import invest.automate.trade.backtest.BacktestEngine;
import invest.automate.trade.strategy.StrategyManager;
import org.junit.jupiter.api.Test;

/**
 * Simple test for the BacktestEngine integration.
 */
public class BacktestEngineTest {

    @Test
    void testBacktestExecution() {
        StrategyManager manager = new StrategyManager();
        BacktestEngine engine = new BacktestEngine(manager);
        engine.run("sample_ticks.csv");
    }
}
