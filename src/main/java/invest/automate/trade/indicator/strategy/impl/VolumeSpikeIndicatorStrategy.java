package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.springframework.stereotype.Component;

@Component
public class VolumeSpikeIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "VOLUME_SPIKE";
    }

    @Override
    public double getValue(BarSeries series, int barIndex) {
        if (barIndex < 20) return 1.0;
        double avg = 0;
        for (int i = barIndex - 19; i <= barIndex; i++)
            avg += series.getBar(i).getVolume().doubleValue();
        avg /= 20;
        double curr = series.getBar(barIndex).getVolume().doubleValue();
        return curr / avg;
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int barIndex) {
        if (barIndex < 20) return Signal.NONE;
        double spike = getValue(series, barIndex);
        if (spike > 2.5) return Signal.BUY; // arbitrary logic, customize as needed
        return Signal.NONE;
    }
}
