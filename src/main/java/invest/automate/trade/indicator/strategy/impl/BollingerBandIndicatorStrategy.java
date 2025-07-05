package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Component;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DoubleNum;

@Component
public class BollingerBandIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "BOLLINGER";
    }

    @Override
    public double getValue(BarSeries series, int idx) {
        // Example: output the %B (where price is relative to band)
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        BollingerBandsMiddleIndicator mid = new BollingerBandsMiddleIndicator(close);
        BollingerBandsUpperIndicator upper = new BollingerBandsUpperIndicator(mid,
                new StandardDeviationIndicator(close, 20), DoubleNum.valueOf(2));
        BollingerBandsLowerIndicator lower = new BollingerBandsLowerIndicator(mid,
                new StandardDeviationIndicator(close, 20), DoubleNum.valueOf(2));
        double price = close.getValue(idx).doubleValue();
        double upperVal = upper.getValue(idx).doubleValue();
        double lowerVal = lower.getValue(idx).doubleValue();
        return (price - lowerVal) / (upperVal - lowerVal + 1e-9); // add small value to prevent division by zero
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int idx) {
        if (idx < 21) return Signal.NONE;
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        BollingerBandsMiddleIndicator mid = new BollingerBandsMiddleIndicator(close);
        BollingerBandsUpperIndicator upper = new BollingerBandsUpperIndicator(mid,
                new StandardDeviationIndicator(close, 20), DoubleNum.valueOf(2));
        BollingerBandsLowerIndicator lower = new BollingerBandsLowerIndicator(mid,
                new StandardDeviationIndicator(close, 20), DoubleNum.valueOf(2));
        double prevPrice = close.getValue(idx - 1).doubleValue();
        double currPrice = close.getValue(idx).doubleValue();
        double lowerBand = lower.getValue(idx).doubleValue();
        double upperBand = upper.getValue(idx).doubleValue();
        // Buy: price bounces up from below lower band
        if (prevPrice < lowerBand && currPrice >= lowerBand) return Signal.BUY;
        // Sell: price falls down from above upper band
        if (prevPrice > upperBand && currPrice <= upperBand) return Signal.SELL;
        return Signal.NONE;
    }
}
