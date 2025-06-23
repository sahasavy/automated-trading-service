package invest.automate.trade.strategy;

import invest.automate.trade.model.Tick;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.signal.SignalDispatcher;
import invest.automate.trade.strategy.base.StrategyConfig;
import invest.automate.trade.strategy.impl.MovingAverageCrossoverStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;

class MovingAverageCrossoverStrategyTest {

    private MovingAverageCrossoverStrategy strategy;
    private SignalDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = mock(SignalDispatcher.class);
        strategy = new MovingAverageCrossoverStrategy();

        Map<String, Object> config = new HashMap<>();
        config.put("instrument", "RELIANCE");
        config.put("shortWindow", 3);
        config.put("longWindow", 5);
        config.put("dispatcher", dispatcher);

        strategy.initialize(StrategyConfig.builder().parameters(config).build());
    }

    @Test
    void testSignalGeneration() {
        double[] prices = {100, 101, 102, 103, 104, 105};

        for (double price : prices) {
            Tick tick = Tick.builder().instrumentToken("RELIANCE").lastTradedPrice(price).timestamp(System.currentTimeMillis()).build();
            strategy.onTick(tick);
        }

        verify(dispatcher, atLeastOnce()).dispatch(any(Signal.class));
    }
}
