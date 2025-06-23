package invest.automate.trade.backtest;

import invest.automate.trade.model.Tick;
import invest.automate.trade.strategy.StrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Executes backtesting using historical tick data from a file.
 */
@Slf4j
@RequiredArgsConstructor
public class BacktestEngine {

    private final StrategyManager strategyManager;

    public void run(String csvFilePath) {
        log.info("Starting backtest with CSV: {}", csvFilePath);
        TickSimulator simulator = new TickSimulator(strategyManager::onTick);
        simulator.simulate(csvFilePath);
        log.info("Backtest completed.");
    }
}
