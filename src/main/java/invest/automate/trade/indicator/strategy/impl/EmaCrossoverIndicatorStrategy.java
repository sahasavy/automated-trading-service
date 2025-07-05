package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Component;

@Component
public class EmaCrossoverIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "EMA_CROSS";
    }

    @Override
    public double getValue(BarSeries series, int idx) {
        EMAIndicator fastEma = new EMAIndicator(new ClosePriceIndicator(series), 9);
        EMAIndicator slowEma = new EMAIndicator(new ClosePriceIndicator(series), 21);
        return fastEma.getValue(idx).doubleValue() - slowEma.getValue(idx).doubleValue();
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int idx) {
        if (idx < 22) return Signal.NONE;
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        EMAIndicator shortEma = new EMAIndicator(close, 9);
        EMAIndicator longEma = new EMAIndicator(close, 21);
        double shortPrev = shortEma.getValue(idx - 1).doubleValue();
        double longPrev = longEma.getValue(idx - 1).doubleValue();
        double shortNow = shortEma.getValue(idx).doubleValue();
        double longNow = longEma.getValue(idx).doubleValue();
        if (shortPrev < longPrev && shortNow >= longNow) return Signal.BUY;
        if (shortPrev > longPrev && shortNow <= longNow) return Signal.SELL;
        return Signal.NONE;
    }
}
