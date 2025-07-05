package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.donchian.DonchianChannelLowerIndicator;
import org.ta4j.core.indicators.donchian.DonchianChannelUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Component;

@Component
public class DonchianChannelIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "DONCHIAN";
    }

    @Override
    public double getValue(BarSeries series, int barIndex) {
        // Optionally return distance to upper/lower
        double close = new ClosePriceIndicator(series).getValue(barIndex).doubleValue();
        double upper = new DonchianChannelUpperIndicator(series, 20).getValue(barIndex).doubleValue();
        double lower = new DonchianChannelLowerIndicator(series, 20).getValue(barIndex).doubleValue();
        return close - upper + close - lower;
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int barIndex) {
        if (barIndex < 21) return Signal.NONE;
        DonchianChannelUpperIndicator upper = new DonchianChannelUpperIndicator(series, 20);
        DonchianChannelLowerIndicator lower = new DonchianChannelLowerIndicator(series, 20);
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        double prevClose = close.getValue(barIndex - 1).doubleValue();
        double currClose = close.getValue(barIndex).doubleValue();
        double upperVal = upper.getValue(barIndex).doubleValue();
        double lowerVal = lower.getValue(barIndex).doubleValue();
        if (prevClose <= upperVal && currClose > upperVal) return Signal.BUY;
        if (prevClose >= lowerVal && currClose < lowerVal) return Signal.SELL;
        return Signal.NONE;
    }
}
