package invest.automate.trade.indicator.strategy.impl;

import invest.automate.trade.indicator.strategy.TradingIndicatorStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Component;

@Component
public class SuperTrendIndicatorStrategy implements TradingIndicatorStrategy {
    @Override
    public String getName() {
        return "SUPER_TREND";
    }

    @Override
    public double getValue(BarSeries series, int idx) {
        return computeSuperTrend(series, idx, 10, 3.0);
    }

    @Override
    public Signal evaluateSignal(BarSeries series, int barIndex) {
        if (barIndex < 15) return Signal.NONE;
        int period = 10;
        double factor = 3.0;
        double[] superTrend = new double[series.getBarCount()];
        boolean[] trendUp = new boolean[series.getBarCount()];

        ClosePriceIndicator close = new ClosePriceIndicator(series);

        superTrend[0] = close.getValue(0).doubleValue();
        trendUp[0] = true;

        for (int i = 1; i <= barIndex; i++) {
            double hl2 = (series.getBar(i).getHighPrice().doubleValue() +
                    series.getBar(i).getLowPrice().doubleValue()) / 2.0;
            ATRIndicator atr = new ATRIndicator(series, period);
            double upperBand = hl2 + factor * atr.getValue(i).doubleValue();
            double lowerBand = hl2 - factor * atr.getValue(i).doubleValue();

            if (close.getValue(i).doubleValue() > superTrend[i - 1]) {
                superTrend[i] = Math.max(lowerBand, superTrend[i - 1]);
                trendUp[i] = true;
            } else {
                superTrend[i] = Math.min(upperBand, superTrend[i - 1]);
                trendUp[i] = false;
            }
        }
        if (barIndex > 0 && !trendUp[barIndex - 1] && trendUp[barIndex]) return Signal.BUY;
        if (barIndex > 0 && trendUp[barIndex - 1] && !trendUp[barIndex]) return Signal.SELL;
        return Signal.NONE;
    }

    private double computeSuperTrend(BarSeries series, int barIndex, int period, double factor) {
        if (barIndex < period) return Double.NaN;
        ATRIndicator atr = new ATRIndicator(series, period);
        double[] superTrend = new double[series.getBarCount()];
        boolean[] trendUp = new boolean[series.getBarCount()];

        ClosePriceIndicator close = new ClosePriceIndicator(series);

        superTrend[0] = close.getValue(0).doubleValue();
        trendUp[0] = true;

        for (int i = 1; i <= barIndex; i++) {
            double hl2 = (series.getBar(i).getHighPrice().doubleValue() +
                    series.getBar(i).getLowPrice().doubleValue()) / 2.0;
            double upperBand = hl2 + factor * atr.getValue(i).doubleValue();
            double lowerBand = hl2 - factor * atr.getValue(i).doubleValue();

            if (close.getValue(i).doubleValue() > superTrend[i - 1]) {
                superTrend[i] = Math.max(lowerBand, superTrend[i - 1]);
                trendUp[i] = true;
            } else {
                superTrend[i] = Math.min(upperBand, superTrend[i - 1]);
                trendUp[i] = false;
            }
        }
        return superTrend[barIndex];
    }
}
