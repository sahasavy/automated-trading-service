package invest.automate.trade.backtest.provider;

import com.zerodhatech.models.Tick;
import invest.automate.trade.config.BacktestConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component("csvFileHistoricDataProvider")
@RequiredArgsConstructor
public class CsvFileHistoricDataProvider implements HistoricDataProvider {

    private final BacktestConfig backtestConfig;

    @Override
    public List<Tick> loadHistoricTicks() throws Exception {
        List<Tick> ticks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(backtestConfig.getCsvPath()))) {
            String header = br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                Tick tick = new Tick();
                tick.setInstrumentToken(Long.parseLong(fields[0]));
                tick.setLastTradedPrice(Double.parseDouble(fields[1]));
                tick.setLastTradedQuantity(Double.parseDouble(fields[2]));
                // TODO - Add timestamp or other fields as per your CSV structure

                ticks.add(tick);
            }
        }
        return ticks;
    }
}
