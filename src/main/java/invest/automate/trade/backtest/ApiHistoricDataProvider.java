package invest.automate.trade.backtest;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.Tick;
import com.zerodhatech.models.HistoricalData;
import invest.automate.trade.config.BacktestConfig;
import invest.automate.trade.service.broker.ZerodhaKiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("apiHistoricDataProvider")
@RequiredArgsConstructor
public class ApiHistoricDataProvider implements HistoricDataProvider {

    private final BacktestConfig backtestConfig;
    private final ZerodhaKiteService zerodhaKiteService;

    @Override
    public List<Tick> loadHistoricTicks() throws Exception {
        KiteConnect kiteConnect = zerodhaKiteService.getKiteConnect();

        List<HistoricalData> historicalData = kiteConnect.getHistoricalData(backtestConfig.getApiInstrumentToken(),
                backtestConfig.getApiFrom(), backtestConfig.getApiTo(), backtestConfig.getApiInterval(), false, false);
        List<Tick> ticks = new ArrayList<>();

        for (HistoricalData data : historicalData) {
            Tick tick = new Tick();
            tick.instrumentToken = instrumentToken;
            tick.lastTradedPrice = data.close;
            tick.lastTradedQuantity = data.volume;
            tick.timestamp = java.sql.Timestamp.valueOf(data.time);
            ticks.add(tick);
        }
        return ticks;
    }
}
