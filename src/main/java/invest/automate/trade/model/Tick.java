package invest.automate.trade.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tick {
    private long instrumentToken;
    private double price;
    private LocalDateTime timestamp;
}
