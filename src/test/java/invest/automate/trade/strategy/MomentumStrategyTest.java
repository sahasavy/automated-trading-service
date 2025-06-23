package invest.automate.trade.strategy;

import invest.automate.trade.model.Tick;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.signal.SignalDispatcher;
import invest.automate.trade.strategy.base.StrategyConfig;
import invest.automate.trade.strategy.impl.MomentumStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;

public class MomentumStrategyTest {

    private MomentumStrategy strategy;
    private SignalDispatcher dispatcher;

    @BeforeEach
    void setup() {
        dispatcher = mock(SignalDispatcher.class);
        strategy = new MomentumStrategy();

        Map<String, Object> config = new HashMap<>();
        config.put("instrument", "RELIANCE");
        config.put("threshold", 1.0);
        config.put("dispatcher", dispatcher);

        strategy.initialize(StrategyConfig.builder().parameters(config).build());
    }

    @Test
    void testSignalTrigger() {
        double[] prices = {100.0, 102.0, 101.5, 99.0, 101.0}; // Significant changes should trigger signals

        for (double price : prices) {
            Tick tick = Tick.builder()
                    .instrumentToken("RELIANCE")
                    .lastTradedPrice(price)
                    .timestamp(System.currentTimeMillis())
                    .build();
            strategy.onTick(tick);
        }

        verify(dispatcher, atLeastOnce()).dispatch(any(Signal.class));
    }
}
