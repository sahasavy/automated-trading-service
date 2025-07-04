package invest.automate.trade.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;

@Service
@RequiredArgsConstructor
public class SignalCompositionService {

    private final IndicatorService indicatorService;
    private final MlModelService mlModelService;

    public enum Signal {
        BUY,
        SELL,
        NONE
    }

    public Signal compositeSignal(BarSeries series) {
        IndicatorService.Signal indicatorSignal = indicatorService.evaluateEmaCrossover(series);

        String mlSignal = "NONE";
        try {
            mlSignal = mlModelService.predict(series);
        } catch (Exception ignored) {
        }

        // Voting logic
        if (indicatorSignal == IndicatorService.Signal.BUY && "UP".equals(mlSignal)) {
            return Signal.BUY;
        }
        if (indicatorSignal == IndicatorService.Signal.SELL && "DOWN".equals(mlSignal)) {
            return Signal.SELL;
        }
        if (indicatorSignal == IndicatorService.Signal.BUY || "UP".equals(mlSignal)) {
            return Signal.BUY; // NOTE: looser: either says BUY
        }
        if (indicatorSignal == IndicatorService.Signal.SELL || "DOWN".equals(mlSignal)) {
            return Signal.SELL;
        }
        return Signal.NONE;
    }
}
