package invest.automate.trade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a tick data structure containing market data at a specific time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tick {
    private String instrumentToken;
    private double lastTradedPrice;
    private long timestamp;
}
