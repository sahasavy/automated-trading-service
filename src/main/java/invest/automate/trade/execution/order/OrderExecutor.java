package invest.automate.trade.execution.order;

import invest.automate.trade.signal.Signal;

/**
 * Interface for executing trade signals.
 */
public interface OrderExecutor {
    void execute(Signal signal);
}
