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
public class MovingAverageCrossoverStrategy extends Strategy {

    private SMAIndicator shortSma;
    private SMAIndicator longSma;
    private static final int SHORT_PERIOD = 5;
    private static final int LONG_PERIOD = 20;
    private Boolean wasShortAbove = null;  // tracks if short MA was above long MA previously

    public MovingAverageCrossoverStrategy() {
        super();
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        this.shortSma = new SMAIndicator(closePrices, SHORT_PERIOD);
        this.longSma = new SMAIndicator(closePrices, LONG_PERIOD);
    }

    @Override
    public Signal generateSignal(Tick tick) {
        addTickToSeries(tick);
        int lastIndex = series.getEndIndex();
        if (lastIndex < 0) {
            return null;
        }
        double shortVal = shortSma.getValue(lastIndex).doubleValue();
        double longVal = longSma.getValue(lastIndex).doubleValue();
        Signal signal = null;
        if (wasShortAbove != null) {
            boolean isShortAbove = shortVal > longVal;
            // Golden cross: short MA was below and now crosses above long MA -> BUY entry
            if (!wasShortAbove && isShortAbove) {
                signal = createSignal(tick, OrderType.BUY, SignalType.ENTRY);
                log.info("MovingAverageCrossoverStrategy signal: ENTRY BUY at price {} (short MA crossed above long MA)", tick.getPrice());
            }
            // Death cross: short MA was above and now crosses below long MA -> SELL exit
            else if (wasShortAbove && !isShortAbove) {
                signal = createSignal(tick, OrderType.SELL, SignalType.EXIT);
                log.info("MovingAverageCrossoverStrategy signal: EXIT SELL at price {} (short MA crossed below long MA)", tick.getPrice());
            }
            wasShortAbove = isShortAbove;
        } else {
            // Initialize wasShortAbove state on first calculation
            wasShortAbove = shortVal > longVal;
        }
        return signal;
    }
}
