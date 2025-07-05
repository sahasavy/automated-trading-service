package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CCIIndicator;
import org.springframework.stereotype.Component;

@Component
public class CciIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "CCI";
    }

    @Override
    public double getValue(BarSeries series, int barIndex) {
        return new CCIIndicator(series, 20).getValue(barIndex).doubleValue();
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int barIndex) {
        if (barIndex < 21) return Signal.NONE;
        CCIIndicator cci = new CCIIndicator(series, 20);
        double prev = cci.getValue(barIndex - 1).doubleValue();
        double curr = cci.getValue(barIndex).doubleValue();
        if (prev < -100 && curr >= -100) return Signal.BUY;
        if (prev > 100 && curr <= 100) return Signal.SELL;
        return Signal.NONE;
    }
}
