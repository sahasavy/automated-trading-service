package invest.automate.trade.execution.order;

import invest.automate.trade.signal.Signal;

public interface OrderExecutor {
    /**
     * Execute an order based on the provided trading signal.
     * This could place a live order or record a paper trade depending on implementation.
     */
    void executeOrder(Signal signal);
}
