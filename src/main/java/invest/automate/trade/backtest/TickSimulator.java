package invest.automate.trade.backtest;

import invest.automate.trade.model.Tick;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Reads tick data from a CSV file and simulates real-time tick streaming.
 */
@Slf4j
public class TickSimulator {

    private final Consumer<Tick> tickHandler;

    public TickSimulator(Consumer<Tick> tickHandler) {
        this.tickHandler = tickHandler;
    }

    public void simulate(String csvFilePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(csvFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Format: timestamp,instrumentToken,ltp
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                Tick tick = Tick.builder()
                        .timestamp(Long.parseLong(parts[0]))
                        .instrumentToken(parts[1])
                        .lastTradedPrice(Double.parseDouble(parts[2]))
                        .build();

                tickHandler.accept(tick);
            }

        } catch (Exception e) {
            log.error("Error during tick simulation", e);
        }
    }
}
