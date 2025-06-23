package invest.automate.trade.strategy.base;

import invest.automate.trade.model.Tick;

/**
 * Interface to be implemented by all trading strategies.
 */
public interface Strategy {
    void initialize(StrategyConfig config);
    void onTick(Tick tick);
}
