package invest.automate.trade.strategy.base;

import invest.automate.trade.model.Tick;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.model.OrderType;
import invest.automate.trade.model.SignalType;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.num.NumFactory;

import java.time.*;

@Slf4j
public abstract class Strategy {
    protected BarSeries series;

    public Strategy() {
        // Initialize an empty time series for price data
        this.series = new BaseBarSeriesBuilder().withName("Series").build();
    }

    /**
     * Processes a new tick (market data point) and returns a trading Signal if conditions are met.
     * Concrete strategy implementations must override this to generate signals.
     *
     * @param tick The new market tick data
     * @return A Signal if generated, or null if no trading signal at this tick
     */
    public abstract Signal generateSignal(Tick tick);

    /**
     * Utility method to add a new tick's data to the BarSeries.
     * Each tick is added as a bar of 1-second duration (for simulation purposes).
     */
    protected void addTickToSeries(Tick tick) {
        Duration timePeriod = Duration.ofSeconds(1);
        Instant endTime = tick.getTimestamp().atZone(ZoneId.systemDefault()).toInstant();

        Num openPrice = DoubleNum.valueOf(tick.getPrice());
        Num highPrice = DoubleNum.valueOf(tick.getPrice());
        Num lowPrice = DoubleNum.valueOf(tick.getPrice());
        Num closePrice = DoubleNum.valueOf(tick.getPrice());
        Num volume = DoubleNum.valueOf(tick.getPrice());
        Num amount = DoubleNum.valueOf(123); //TODO - What to do with this?
        long trades = 123; //TODO - What to do with this?

        // Use BarBuilder to create a Bar and add to the series
        Bar bar = new BaseBar(timePeriod, endTime, openPrice, highPrice, lowPrice, closePrice, volume, amount, trades);

        series.addBar(bar);
    }

    /**
     * Helper to construct a Signal object.
     */
    protected Signal createSignal(Tick tick, OrderType orderType, SignalType signalType) {
        return new Signal(tick.getInstrumentToken(), orderType, signalType, tick.getPrice(), tick.getTimestamp());
    }
}
