package invest.automate.trade.signal;

import java.time.LocalDateTime;
import invest.automate.trade.model.OrderType;
import invest.automate.trade.model.SignalType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Signal {
    private long instrumentToken;
    private OrderType orderType;
    private SignalType signalType;
    private double price;
    private LocalDateTime timestamp;
}
