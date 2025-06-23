package invest.automate.trade.execution.order;

import invest.automate.trade.signal.Signal;
import lombok.extern.slf4j.Slf4j;

/**
 * Executes trade signals using Zerodha APIs (SDK integration placeholder).
 */
@Slf4j
public class ZerodhaOrderExecutor implements OrderExecutor {

    @Override
    public void execute(Signal signal) {
        // Placeholder for Zerodha order logic
        log.info("Executing order via Zerodha: {}", signal);
    }
}
