package invest.automate.trade.strategy.impl;

import invest.automate.trade.model.OrderType;
import invest.automate.trade.model.SignalType;
import invest.automate.trade.model.Tick;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.strategy.base.Strategy;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MomentumStrategy extends Strategy {

    private SMAIndicator sma;
    private static final int MA_PERIOD = 20;
    private Boolean wasBelowMA = null;  // tracks if price was below MA on previous tick

    public MomentumStrategy() {
        super();
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        this.sma = new SMAIndicator(closePrices, MA_PERIOD);
    }

    @Override
    public Signal generateSignal(Tick tick) {
        addTickToSeries(tick);
        int lastIndex = series.getEndIndex();
        if (lastIndex < 0) {
            return null;
        }
        double price = tick.getPrice();
        double maValue = sma.getValue(lastIndex).doubleValue();
        Signal signal = null;
        if (wasBelowMA != null) {
            // Check for price crossing above MA
            if (wasBelowMA && price >= maValue) {
                signal = createSignal(tick, OrderType.BUY, SignalType.ENTRY);
                log.info("MomentumStrategy signal: ENTRY BUY at price {} (price crossed above MA)", price);
            }
            // Check for price crossing below MA
            else if (!wasBelowMA && price < maValue) {
                signal = createSignal(tick, OrderType.SELL, SignalType.EXIT);
                log.info("MomentumStrategy signal: EXIT SELL at price {} (price crossed below MA)", price);
            }
        }
        // Update state: was price below MA at this tick?
        wasBelowMA = (price < maValue);
        return signal;
    }
}
