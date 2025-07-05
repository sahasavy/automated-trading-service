package invest.automate.trade.indicator.strategy;

import org.ta4j.core.BarSeries;

public interface TradingIndicatorStrategy {
    String getName(); // E.g. "RSI", "MACD", etc.

    double getValue(BarSeries series, int barIndex); // for ML/features

    Signal evaluateSignal(BarSeries series, int barIndex); // for trading signal

    enum Signal {
        BUY,
        SELL,
        NONE
    }
}
