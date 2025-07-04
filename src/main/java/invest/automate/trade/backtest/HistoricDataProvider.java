package invest.automate.trade.backtest;

import com.zerodhatech.models.Tick;

import java.util.List;

public interface HistoricDataProvider {
    List<Tick> loadHistoricTicks() throws Exception;
}
