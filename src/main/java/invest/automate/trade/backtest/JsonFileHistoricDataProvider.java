package invest.automate.trade.backtest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerodhatech.models.Tick;
import invest.automate.trade.config.BacktestConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component("jsonFileHistoricDataProvider")
@RequiredArgsConstructor
public class JsonFileHistoricDataProvider implements HistoricDataProvider {

    private final BacktestConfig backtestConfig;

    @Override
    public List<Tick> loadHistoricTicks() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(backtestConfig.getJsonPath()), new TypeReference<List<Tick>>() {
        });
    }
}
