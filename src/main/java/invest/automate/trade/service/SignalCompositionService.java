package invest.automate.trade.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;

@Service
@RequiredArgsConstructor
public class SignalCompositionService {

    private final IndicatorService indicatorService;
    private final WekaModelService wekaModelService;

    public enum Signal {BUY, SELL, NONE}

    public Signal compositeSignal(BarSeries series) {
        // TA4J indicator logic
        IndicatorService.Signal ta4j = indicatorService.evaluateEmaCrossover(series);
        // ML logic
        String ml = "NONE";
        try {
            ml = wekaModelService.predict(series);
        } catch (Exception ignored) {
        }
        // Voting logic
        if (ta4j == IndicatorService.Signal.BUY && "UP".equals(ml)) return Signal.BUY;
        if (ta4j == IndicatorService.Signal.SELL && "DOWN".equals(ml)) return Signal.SELL;
        if (ta4j == IndicatorService.Signal.BUY || "UP".equals(ml)) return Signal.BUY; // looser: either says BUY
        if (ta4j == IndicatorService.Signal.SELL || "DOWN".equals(ml)) return Signal.SELL;
        return Signal.NONE;
    }
}
