package invest.automate.trade.signal;

import invest.automate.trade.execution.order.OrderExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignalDispatcher {

    private OrderExecutor orderExecutor;

    public SignalDispatcher(OrderExecutor orderExecutor) {
        this.orderExecutor = orderExecutor;
    }

    /**
     * Dispatches the given signal to the configured OrderExecutor.
     */
    public void dispatch(Signal signal) {
        log.info("Dispatching signal: {} {} at {}",
                signal.getSignalType(), signal.getOrderType(), signal.getPrice());
        orderExecutor.executeOrder(signal);
    }
}
