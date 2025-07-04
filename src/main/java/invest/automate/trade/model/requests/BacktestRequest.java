package invest.automate.trade.model.requests;

import lombok.Data;

@Data
public class BacktestRequest {
    private String dataFile; // Optional: override file for backtest
    // ...other params
}

