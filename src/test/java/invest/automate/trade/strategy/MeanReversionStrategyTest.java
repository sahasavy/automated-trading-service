package invest.automate.trade.strategy;

import invest.automate.trade.model.Tick;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.signal.SignalDispatcher;
import invest.automate.trade.strategy.base.StrategyConfig;
import invest.automate.trade.strategy.impl.MeanReversionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;

public class MeanReversionStrategyTest {

    private MeanReversionStrategy strategy;
    private SignalDispatcher dispatcher;

    @BeforeEach
    void setup() {
        dispatcher = mock(SignalDispatcher.class);
        strategy = new MeanReversionStrategy();

        Map<String, Object> config = new HashMap<>();
        config.put("instrument", "RELIANCE");
        config.put("window", 5);
        config.put("threshold", 2.0);
        config.put("dispatcher", dispatcher);

        strategy.initialize(StrategyConfig.builder().parameters(config).build());
    }

    @Test
    void testBuySellSignals() {
        double[] prices = {100, 101, 99, 98, 97, 105}; // Drop below average -> BUY, then rise -> SELL

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
