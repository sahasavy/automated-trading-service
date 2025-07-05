package invest.automate.trade.service;

import invest.automate.trade.indicator.IndicatorRegistryService;
import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import invest.automate.trade.ml.MlModelService;
import org.ta4j.core.BarSeries;
import weka.core.Instance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignalCompositionService {

    private final IndicatorRegistryService indicatorRegistryService;
    private final MlModelService mlModelService;

    public enum Signal {
        BUY,
        SELL,
        NONE
    }

    /**
     * Compose a signal based on TA4J indicator and ML ensemble outputs.
     *
     * @param series   The current BarSeries
     * @param barIndex The index for which to calculate
     * @param instance The Weka instance with all features (from WekaInstanceBuilder)
     * @return BUY, SELL, or NONE
     */
    public Signal composeSignal(BarSeries series, int barIndex, Instance instance) throws Exception {
        // --- TA4J signals ---
        TradingIndicatorStrategy.Signal indicatorSignal = indicatorRegistryService.votingSignal(series, barIndex);

        // --- ML signals (ensemble) ---
        Map<String, String> mlPredictions = mlModelService.predictAll(instance);
        long buys = mlPredictions.values().stream().filter("UP"::equalsIgnoreCase).count();
        long sells = mlPredictions.values().stream().filter("DOWN"::equalsIgnoreCase).count();
        String mlSignal = (buys > sells) ? "UP" : (sells > buys ? "DOWN" : "NONE");

        // --- Compose (example: both must agree, or indicator wins, or majority wins) ---
        if (indicatorSignal == TradingIndicatorStrategy.Signal.BUY && "UP".equals(mlSignal)) {
            return Signal.BUY;
        }
        if (indicatorSignal == TradingIndicatorStrategy.Signal.SELL && "DOWN".equals(mlSignal)) {
            return Signal.SELL;
        }
        // Or: if either says BUY, return BUY (looser)
        if (indicatorSignal == TradingIndicatorStrategy.Signal.BUY || "UP".equals(mlSignal)) {
            return Signal.BUY;
        }
        if (indicatorSignal == TradingIndicatorStrategy.Signal.SELL || "DOWN".equals(mlSignal)) {
            return Signal.SELL;
        }
        return Signal.NONE;
    }
}
