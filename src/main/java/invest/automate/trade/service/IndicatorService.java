package invest.automate.trade.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.BaseStrategy;

@Slf4j
@Service
public class IndicatorService {

    public enum Signal {
        BUY,
        SELL,
        NONE
    }

    public Signal evaluateEmaCrossover(BarSeries series) {
        if (series == null || series.getBarCount() < 22) {
            return Signal.NONE;
        }

        ClosePriceIndicator close = new ClosePriceIndicator(series);
        EMAIndicator shortEma = new EMAIndicator(close, 9);
        EMAIndicator longEma = new EMAIndicator(close, 21);

        Strategy strategy = new BaseStrategy(
                new CrossedUpIndicatorRule(shortEma, longEma),
                new CrossedDownIndicatorRule(shortEma, longEma)
        );

        int end = series.getEndIndex();
        if (strategy.shouldEnter(end)) {
            return Signal.BUY;
        }
        if (strategy.shouldExit(end)) {
            return Signal.SELL;
        }
        return Signal.NONE;
    }
}
