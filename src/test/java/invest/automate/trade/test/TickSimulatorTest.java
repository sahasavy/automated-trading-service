package invest.automate.trade.test;

import invest.automate.trade.backtest.TickSimulator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple test for the TickSimulator using sample_ticks.csv
 */
public class TickSimulatorTest {

    @Test
    void testSimulate() {
        AtomicInteger count = new AtomicInteger(0);
        TickSimulator simulator = new TickSimulator(tick -> {
            count.incrementAndGet();
            assertTrue(tick.getInstrumentToken() != null);
        });

        simulator.simulate("sample_ticks.csv");
        assertTrue(count.get() > 0);
    }
}
