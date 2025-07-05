package invest.automate.trade.indicator;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;

import java.util.*;

@Service
@RequiredArgsConstructor
public class IndicatorRegistryService {

    // All indicator strategies are auto-wired
    private final List<TradingIndicatorStrategy> strategies;

    // For ML feature building
    public Map<String, Double> getAllIndicatorValues(BarSeries series, int barIndex) {
        Map<String, Double> result = new HashMap<>();
        for (TradingIndicatorStrategy strategy : strategies) {
            result.put(strategy.getName(), strategy.getValue(series, barIndex));
        }
        return result;
    }

    // For trading/voting logic
    public TradingIndicatorStrategy.Signal votingSignal(BarSeries series, int barIndex) {
        int buyVotes = 0, sellVotes = 0;
        for (TradingIndicatorStrategy strategy : strategies) {
            TradingIndicatorStrategy.Signal signal = strategy.evaluateSignal(series, barIndex);
            if (signal == TradingIndicatorStrategy.Signal.BUY) buyVotes++;
            if (signal == TradingIndicatorStrategy.Signal.SELL) sellVotes++;
        }
        if (buyVotes > sellVotes && buyVotes > 0) return TradingIndicatorStrategy.Signal.BUY;
        if (sellVotes > buyVotes && sellVotes > 0) return TradingIndicatorStrategy.Signal.SELL;
        return TradingIndicatorStrategy.Signal.NONE;
    }

    // Optional: get strategy by name
    public Optional<TradingIndicatorStrategy> getStrategyByName(String name) {
        return strategies.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst();
    }
}
