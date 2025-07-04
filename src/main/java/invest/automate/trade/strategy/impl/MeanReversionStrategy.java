package invest.automate.trade.strategy.impl;

import invest.automate.trade.model.OrderType;
import invest.automate.trade.model.SignalType;
import invest.automate.trade.model.Tick;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.strategy.base.Strategy;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MeanReversionStrategy extends Strategy {

    private RSIIndicator rsi;
    private static final int RSI_PERIOD = 14;
    private Double prevRsi = null;  // store previous RSI value for threshold crossing detection

    public MeanReversionStrategy() {
        super();
        // Initialize RSI indicator on the series
        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);
        this.rsi = new RSIIndicator(closePrices, RSI_PERIOD);
    }

    @Override
    public Signal generateSignal(Tick tick) {
        addTickToSeries(tick);
        int lastIndex = series.getEndIndex();
        if (lastIndex < 0) {
            return null;  // no data
        }
        double currentRsi = rsi.getValue(lastIndex).doubleValue();
        Signal signal = null;
        if (prevRsi != null) {
            // Generate entry signal: RSI crossing above 30 (oversold -> normal)
            if (prevRsi < 30.0 && currentRsi >= 30.0) {
                signal = createSignal(tick, OrderType.BUY, SignalType.ENTRY);
                log.info("MeanReversionStrategy signal: ENTRY BUY at price {} (RSI crossed above 30)", tick.getPrice());
            }
            // Generate exit signal: RSI crossing below 70 (overbought -> normal)
            else if (prevRsi > 70.0 && currentRsi <= 70.0) {
                signal = createSignal(tick, OrderType.SELL, SignalType.EXIT);
                log.info("MeanReversionStrategy signal: EXIT SELL at price {} (RSI crossed below 70)", tick.getPrice());
            }
        }
        // Update previous RSI for next tick
        prevRsi = currentRsi;
        return signal;
    }
}
