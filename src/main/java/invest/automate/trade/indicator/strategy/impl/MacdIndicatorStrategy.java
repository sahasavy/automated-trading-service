package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Component;

@Component
public class MacdIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "MACD";
    }

    @Override
    public double getValue(BarSeries series, int barIndex) {
        return new MACDIndicator(new ClosePriceIndicator(series), 12, 26)
                .getValue(barIndex).doubleValue();
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int barIndex) {
        if (barIndex < 35) return Signal.NONE;
        MACDIndicator macd = new MACDIndicator(new ClosePriceIndicator(series), 12, 26);
        EMAIndicator signal = new EMAIndicator(macd, 9);
        double macdPrev = macd.getValue(barIndex - 1).doubleValue();
        double signalPrev = signal.getValue(barIndex - 1).doubleValue();
        double macdNow = macd.getValue(barIndex).doubleValue();
        double signalNow = signal.getValue(barIndex).doubleValue();
        if (macdPrev < signalPrev && macdNow >= signalNow) return Signal.BUY;
        if (macdPrev > signalPrev && macdNow <= signalNow) return Signal.SELL;
        return Signal.NONE;
    }
}
