package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Component;

@Component
public class SmaCrossoverIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "SMA_CROSS";
    }

    @Override
    public double getValue(BarSeries series, int barIndex) {
        // Optionally return fast minus slow for feature
        double fast = new SMAIndicator(new ClosePriceIndicator(series), 10).getValue(barIndex).doubleValue();
        double slow = new SMAIndicator(new ClosePriceIndicator(series), 50).getValue(barIndex).doubleValue();
        return fast - slow;
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int barIndex) {
        if (barIndex < 50) return Signal.NONE;
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        SMAIndicator fastSma = new SMAIndicator(close, 10);
        SMAIndicator slowSma = new SMAIndicator(close, 50);
        double fastPrev = fastSma.getValue(barIndex - 1).doubleValue();
        double slowPrev = slowSma.getValue(barIndex - 1).doubleValue();
        double fastNow = fastSma.getValue(barIndex).doubleValue();
        double slowNow = slowSma.getValue(barIndex).doubleValue();
        if (fastPrev < slowPrev && fastNow >= slowNow) return Signal.BUY;
        if (fastPrev > slowPrev && fastNow <= slowNow) return Signal.SELL;
        return Signal.NONE;
    }
}
