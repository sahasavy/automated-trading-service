package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Component;

/**
 * RSI Overbought/Oversold
 * BUY: RSI crosses above 30 (from below)
 * SELL: RSI crosses below 70 (from above)
 */
@Component
public class RsiIndicatorStrategy implements TradingIndicatorStrategy {

    @Override
    public String getName() {
        return "RSI";
    }

    @Override
    public double getValue(BarSeries series, int barIndex) {
        return new RSIIndicator(new ClosePriceIndicator(series), 14)
                .getValue(barIndex).doubleValue();
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int barIndex) {
//        int barIndex = series.getEndIndex(); // TODO - Check this
        if (barIndex < 15) {
            return Signal.NONE;
        }
        RSIIndicator rsi = new RSIIndicator(new ClosePriceIndicator(series), 14);
        double prev = rsi.getValue(barIndex - 1).doubleValue();
        double curr = rsi.getValue(barIndex).doubleValue();
        if (prev < 30 && curr >= 30) {
            return Signal.BUY;
        }
        if (prev > 70 && curr <= 70) {
            return Signal.SELL;
        }
        return Signal.NONE;
    }
}
