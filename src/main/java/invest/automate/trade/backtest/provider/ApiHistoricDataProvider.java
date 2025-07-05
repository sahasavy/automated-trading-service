package invest.automate.trade.backtest.provider;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Tick;
import com.zerodhatech.models.HistoricalData;
import invest.automate.trade.config.BacktestConfig;
import invest.automate.trade.service.broker.ZerodhaKiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("apiHistoricDataProvider")
@RequiredArgsConstructor
public class ApiHistoricDataProvider implements HistoricDataProvider {

    private final BacktestConfig backtestConfig;
    private final ZerodhaKiteService zerodhaKiteService;

    @Override
    public List<Tick> loadHistoricTicks() throws Exception {
        KiteConnect kiteConnect = zerodhaKiteService.getKiteConnect();
        Date fromDate = java.sql.Date.valueOf(backtestConfig.getApiFrom());
        Date toDate = java.sql.Date.valueOf(backtestConfig.getApiTo());

        HistoricalData historicalData = null;
        try {
            historicalData = kiteConnect.getHistoricalData(fromDate, toDate,
                    backtestConfig.getApiInstrumentToken(), backtestConfig.getApiInterval(), false, false);
        } catch (KiteException e) {
            throw new RuntimeException(e);
        }
        List<Tick> ticks = new ArrayList<>();

        for (HistoricalData data : historicalData.dataArrayList) {
            // TODO - Check this
            Tick tick = new Tick();
            tick.setInstrumentToken(Long.parseLong(backtestConfig.getApiInstrumentToken()));
            tick.setTickTimestamp(DateFormat.getDateInstance().parse(data.timeStamp));
            tick.setOpenPrice(data.open);
            tick.setHighPrice(data.high);
            tick.setLowPrice(data.low);

            tick.setClosePrice(data.close);
            tick.setLastTradedPrice(data.close);

            tick.setVolumeTradedToday(data.volume);
            tick.setLastTradedQuantity(data.volume);

            tick.setOi(data.oi);

            ticks.add(tick);
        }
        return ticks;
    }
}
