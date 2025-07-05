package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.springframework.stereotype.Component;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;

@Component
public class AdxIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "ADX";
    }

    @Override
    public double getValue(BarSeries series, int barIndex) {
        return new ADXIndicator(series, 14).getValue(barIndex).doubleValue();
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int barIndex) {
        if (barIndex < 15) return Signal.NONE;
        ADXIndicator adx = new ADXIndicator(series, 14);
        PlusDIIndicator plusDI = new PlusDIIndicator(series, 14);
        MinusDIIndicator minusDI = new MinusDIIndicator(series, 14);
        double adxValue = adx.getValue(barIndex).doubleValue();
        double plus = plusDI.getValue(barIndex).doubleValue();
        double minus = minusDI.getValue(barIndex).doubleValue();
        if (adxValue > 25) {
            if (plus > minus) return Signal.BUY;
            if (minus > plus) return Signal.SELL;
        }
        return Signal.NONE;
    }
}
