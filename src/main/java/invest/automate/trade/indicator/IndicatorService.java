//package invest.automate.trade.indicator;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.ta4j.core.BarSeries;
//import org.ta4j.core.Strategy;
//import org.ta4j.core.indicators.ATRIndicator;
//import org.ta4j.core.indicators.CCIIndicator;
//import org.ta4j.core.indicators.MACDIndicator;
//import org.ta4j.core.indicators.RSIIndicator;
//import org.ta4j.core.indicators.adx.ADXIndicator;
//import org.ta4j.core.indicators.adx.MinusDIIndicator;
//import org.ta4j.core.indicators.adx.PlusDIIndicator;
//import org.ta4j.core.indicators.averages.EMAIndicator;
//import org.ta4j.core.indicators.averages.SMAIndicator;
//import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
//import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
//import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
//import org.ta4j.core.indicators.donchian.DonchianChannelLowerIndicator;
//import org.ta4j.core.indicators.donchian.DonchianChannelUpperIndicator;
//import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
//import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
//import org.ta4j.core.num.DoubleNum;
//import org.ta4j.core.num.Num;
//import org.ta4j.core.rules.CrossedUpIndicatorRule;
//import org.ta4j.core.rules.CrossedDownIndicatorRule;
//import org.ta4j.core.BaseStrategy;
//import org.ta4j.core.*;
//
//import java.util.*;
//
//@Slf4j
//@Service
//public class IndicatorService {
//
//    public enum Signal {
//        BUY,
//        SELL,
//        NONE
//    }
//
//    // --- All indicator value extraction for ML/features ---
//    public Map<String, Double> getAllIndicatorValues(BarSeries series, int barIndex) {
//        Map<String, Double> indicators = new HashMap<>();
//
//        // --- Basic TA4J indicators ---
//        ClosePriceIndicator close = new ClosePriceIndicator(series);
//
//        indicators.put("RSI", safeValue(new RSIIndicator(close, 14), barIndex));
//        indicators.put("MACD", safeValue(new MACDIndicator(close, 12, 26), barIndex));
//        indicators.put("MACD_SIGNAL", safeValue(new EMAIndicator(new MACDIndicator(close, 12, 26), 9), barIndex));
//        indicators.put("SMA_10", safeValue(new SMAIndicator(close, 10), barIndex));
//        indicators.put("SMA_50", safeValue(new SMAIndicator(close, 50), barIndex));
//        indicators.put("BOLL_UPPER", safeValue(new BollingerBandsUpperIndicator(
//                new BollingerBandsMiddleIndicator(close, 20),
//                new StandardDeviationIndicator(close, 20), 2), barIndex));
//        indicators.put("BOLL_LOWER", safeValue(new BollingerBandsLowerIndicator(
//                new BollingerBandsMiddleIndicator(close, 20),
//                new StandardDeviationIndicator(close, 20), 2), barIndex));
//        indicators.put("ADX", safeValue(new ADXIndicator(series, 14), barIndex));
//        indicators.put("PLUS_DI", safeValue(new PlusDIIndicator(series, 14), barIndex));
//        indicators.put("MINUS_DI", safeValue(new MinusDIIndicator(series, 14), barIndex));
//        indicators.put("CCI", safeValue(new CCIIndicator(series, 20), barIndex));
//        indicators.put("DONCHIAN_UPPER", safeValue(new DonchianChannelUpperIndicator(series, 20), barIndex));
//        indicators.put("DONCHIAN_LOWER", safeValue(new DonchianChannelLowerIndicator(series, 20), barIndex));
//
//        // --- Volume spike feature (current bar/avg) ---
//        if (barIndex >= 20) {
//            double avgVol = 0;
//            for (int i = barIndex - 19; i <= barIndex; i++) avgVol += series.getBar(i).getVolume().doubleValue();
//            avgVol /= 20;
//            double currVol = series.getBar(barIndex).getVolume().doubleValue();
//            indicators.put("VOLUME_SPIKE", currVol / avgVol);
//        } else {
//            indicators.put("VOLUME_SPIKE", 1.0);
//        }
//
//        // --- SuperTrend (basic version) ---
//        indicators.put("SUPER_TREND", computeSuperTrend(series, barIndex, 10, 3.0));
//
//        return indicators;
//    }
//
//    private Double safeValue(Indicator<?> ind, int idx) {
//        try {
//            if (idx < 0 || idx >= ind.getBarSeries().getBarCount()) return Double.NaN;
//            return ((Num) ind.getValue(idx)).doubleValue();
//        } catch (Exception e) {
//            return Double.NaN;
//        }
//    }
//
//    // --- Example: voting logic for your strategies ---
//    public Signal votingSignal(BarSeries series, int idx) {
//        int buyVotes = 0, sellVotes = 0;
//        if (evaluateEmaCrossover(series) == Signal.BUY) buyVotes++;
//        if (evaluateRsiStrategy(series) == Signal.BUY) buyVotes++;
//        if (evaluateMacdStrategy(series) == Signal.BUY) buyVotes++;
//        if (evaluateSmaCrossoverStrategy(series) == Signal.BUY) buyVotes++;
//        if (evaluateAdxStrategy(series) == Signal.BUY) buyVotes++;
//        if (evaluateDonchianChannelStrategy(series) == Signal.BUY) buyVotes++;
//        if (evaluateCciStrategy(series) == Signal.BUY) buyVotes++;
//        if (evaluateSuperTrendStrategy(series) == Signal.BUY) buyVotes++;
//
//        if (evaluateEmaCrossover(series) == Signal.SELL) sellVotes++;
//        if (evaluateRsiStrategy(series) == Signal.SELL) sellVotes++;
//        if (evaluateMacdStrategy(series) == Signal.SELL) sellVotes++;
//        if (evaluateSmaCrossoverStrategy(series) == Signal.SELL) sellVotes++;
//        if (evaluateAdxStrategy(series) == Signal.SELL) sellVotes++;
//        if (evaluateDonchianChannelStrategy(series) == Signal.SELL) sellVotes++;
//        if (evaluateCciStrategy(series) == Signal.SELL) sellVotes++;
//        if (evaluateSuperTrendStrategy(series) == Signal.SELL) sellVotes++;
//
//        if (buyVotes > sellVotes && buyVotes > 0) return Signal.BUY;
//        if (sellVotes > buyVotes && sellVotes > 0) return Signal.SELL;
//        return Signal.NONE;
//    }
//
//    /**
//     * EmaCrossover
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateEmaCrossover(BarSeries series) {
//        if (series == null || series.getBarCount() < 22) {
//            return Signal.NONE;
//        }
//
//        ClosePriceIndicator close = new ClosePriceIndicator(series);
//        EMAIndicator shortEma = new EMAIndicator(close, 9);
//        EMAIndicator longEma = new EMAIndicator(close, 21);
//
//        Strategy strategy = new BaseStrategy(
//                new CrossedUpIndicatorRule(shortEma, longEma),
//                new CrossedDownIndicatorRule(shortEma, longEma)
//        );
//
//        int end = series.getEndIndex();
//        if (strategy.shouldEnter(end)) {
//            return Signal.BUY;
//        }
//        if (strategy.shouldExit(end)) {
//            return Signal.SELL;
//        }
//        return Signal.NONE;
//    }
//
//    /**
//     * RSI Overbought/Oversold
//     * BUY: RSI crosses above 30 (from below)
//     * SELL: RSI crosses below 70 (from above)
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateRsiStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 15) return Signal.NONE;
//        RSIIndicator rsi = new RSIIndicator(new ClosePriceIndicator(series), 14);
//        double prev = rsi.getValue(index - 1).doubleValue();
//        double curr = rsi.getValue(index).doubleValue();
//        if (prev < 30 && curr >= 30) return Signal.BUY;
//        if (prev > 70 && curr <= 70) return Signal.SELL;
//        return Signal.NONE;
//    }
//
//    /**
//     * MACD Signal Line Crossover
//     * BUY: MACD crosses above signal line
//     * SELL: MACD crosses below signal line
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateMacdStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 35) {
//            return Signal.NONE;
//        }
//
//        MACDIndicator macd = new MACDIndicator(new ClosePriceIndicator(series), 12, 26);
//        EMAIndicator signal = new EMAIndicator(macd, 9);
//        double macdPrev = macd.getValue(index - 1).doubleValue();
//        double signalPrev = signal.getValue(index - 1).doubleValue();
//        double macdNow = macd.getValue(index).doubleValue();
//        double signalNow = signal.getValue(index).doubleValue();
//
//        if (macdPrev < signalPrev && macdNow >= signalNow) {
//            return Signal.BUY;
//        }
//        if (macdPrev > signalPrev && macdNow <= signalNow) {
//            return Signal.SELL;
//        }
//        return Signal.NONE;
//    }
//
//    /**
//     * Bollinger Bands Squeeze
//     * BUY: Price crosses above lower band
//     * SELL: Price crosses below upper band
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateBollingerBandStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 21) {
//            return Signal.NONE;
//        }
//
//        ClosePriceIndicator close = new ClosePriceIndicator(series);
//        BollingerBandsMiddleIndicator mid = new BollingerBandsMiddleIndicator(close);
//        BollingerBandsUpperIndicator upper = new BollingerBandsUpperIndicator(mid,
//                new StandardDeviationIndicator(close, 20), DoubleNum.valueOf(2));
//        BollingerBandsLowerIndicator lower = new BollingerBandsLowerIndicator(mid,
//                new StandardDeviationIndicator(close, 20), DoubleNum.valueOf(2));
//
//        double prevPrice = close.getValue(index - 1).doubleValue();
//        double currPrice = close.getValue(index).doubleValue();
//        double lowerBand = lower.getValue(index).doubleValue();
//        double upperBand = upper.getValue(index).doubleValue();
//
//        // Buy: price bounces up from below lower band
//        if (prevPrice < lowerBand && currPrice >= lowerBand) {
//            return Signal.BUY;
//        }
//
//        // Sell: price falls down from above upper band
//        if (prevPrice > upperBand && currPrice <= upperBand) {
//            return Signal.SELL;
//        }
//        return Signal.NONE;
//    }
//
//    /**
//     * ADX Trend Strength
//     * BUY: ADX rising above 25 and DI+ > DI-
//     * SELL: ADX rising above 25 and DI- > DI+
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateAdxStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 15) return Signal.NONE;
//        ADXIndicator adx = new ADXIndicator(series, 14);
//        PlusDIIndicator plusDI = new PlusDIIndicator(series, 14);
//        MinusDIIndicator minusDI = new MinusDIIndicator(series, 14);
//        double adxValue = adx.getValue(index).doubleValue();
//        double plus = plusDI.getValue(index).doubleValue();
//        double minus = minusDI.getValue(index).doubleValue();
//        if (adxValue > 25) {
//            if (plus > minus) return Signal.BUY;
//            if (minus > plus) return Signal.SELL;
//        }
//        return Signal.NONE;
//    }
//
//    /**
//     * Volume Spike
//     * BUY/SELL: If current bar’s volume is N times the average of last X bars
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateVolumeSpikeStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 20) return Signal.NONE;
//        double avg = 0;
//        for (int i = index - 19; i < index; i++)
//            avg += series.getBar(i).getVolume().doubleValue();
//        avg /= 20;
//        double curr = series.getLastBar().getVolume().doubleValue();
//        if (curr > 2.5 * avg) {
//            // You can choose to look at price direction too
//            return Signal.BUY; // Or SELL based on context
//        }
//        return Signal.NONE;
//    }
//
//    /**
//     * SMA Crossover
//     * Classic: Buy when fast SMA crosses above slow SMA, Sell when it crosses below.
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateSmaCrossoverStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 50) return Signal.NONE;
//        ClosePriceIndicator close = new ClosePriceIndicator(series);
//        SMAIndicator fastSma = new SMAIndicator(close, 10); // e.g., 10-period
//        SMAIndicator slowSma = new SMAIndicator(close, 50); // e.g., 50-period
//        double fastPrev = fastSma.getValue(index - 1).doubleValue();
//        double slowPrev = slowSma.getValue(index - 1).doubleValue();
//        double fastNow = fastSma.getValue(index).doubleValue();
//        double slowNow = slowSma.getValue(index).doubleValue();
//        if (fastPrev < slowPrev && fastNow >= slowNow) return Signal.BUY;
//        if (fastPrev > slowPrev && fastNow <= slowNow) return Signal.SELL;
//        return Signal.NONE;
//    }
//
//    /**
//     * Donchian Channel Breakout
//     * Buy if price closes above highest high of N bars, Sell if closes below lowest low.
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateDonchianChannelStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 21) return Signal.NONE;
//        DonchianChannelUpperIndicator upper = new DonchianChannelUpperIndicator(series, 20);
//        DonchianChannelLowerIndicator lower = new DonchianChannelLowerIndicator(series, 20);
//        ClosePriceIndicator close = new ClosePriceIndicator(series);
//        double prevClose = close.getValue(index - 1).doubleValue();
//        double currClose = close.getValue(index).doubleValue();
//        double upperVal = upper.getValue(index).doubleValue();
//        double lowerVal = lower.getValue(index).doubleValue();
//        if (prevClose <= upperVal && currClose > upperVal) return Signal.BUY;
//        if (prevClose >= lowerVal && currClose < lowerVal) return Signal.SELL;
//        return Signal.NONE;
//    }
//
//    /**
//     * SuperTrend Indicator
//     * Note: TODO - For real production, keep a class-level buffer for SuperTrend series so you don’t
//     * recalculate everything each call, but the above works for up to a few hundred bars.
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateSuperTrendStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 15) return Signal.NONE;
//        double factor = 3; // common default
//        int period = 10;   // common default
//
//        ATRIndicator atr = new ATRIndicator(series, period);
//        ClosePriceIndicator close = new ClosePriceIndicator(series);
//
//        // TODO - Check these
//        // You need to keep a SuperTrend value per bar. Here is a simple stateful demo:
//        // For robust trading, you’d cache this in a member variable, but for this function call:
//        double[] superTrend = new double[series.getBarCount()];
//        boolean[] trendUp = new boolean[series.getBarCount()];
//
//        superTrend[0] = close.getValue(0).doubleValue();
//        trendUp[0] = true;
//        for (int i = 1; i < series.getBarCount(); i++) {
//            double hl2 = (series.getBar(i).getHighPrice().doubleValue() +
//                    series.getBar(i).getLowPrice().doubleValue()) / 2.0;
//            double upperBand = hl2 + factor * atr.getValue(i).doubleValue();
//            double lowerBand = hl2 - factor * atr.getValue(i).doubleValue();
//
//            if (close.getValue(i).doubleValue() > superTrend[i - 1]) {
//                superTrend[i] = Math.max(lowerBand, superTrend[i - 1]);
//                trendUp[i] = true;
//            } else {
//                superTrend[i] = Math.min(upperBand, superTrend[i - 1]);
//                trendUp[i] = false;
//            }
//        }
//
//        // Buy when trend flips up, sell when flips down
//        if (!trendUp[index - 1] && trendUp[index]) return Signal.BUY;
//        if (trendUp[index - 1] && !trendUp[index]) return Signal.SELL;
//        return Signal.NONE;
//    }
//
//    /**
//     * CCI (Commodity Channel Index) Reversal
//     * Buy if CCI crosses above -100 (from below), sell if CCI crosses below +100
//     *
//     * @param series
//     * @return
//     */
//    public Signal evaluateCciStrategy(BarSeries series) {
//        int index = series.getEndIndex();
//        if (index < 21) return Signal.NONE;
//        CCIIndicator cci = new CCIIndicator(series, 20);
//        double prev = cci.getValue(index - 1).doubleValue();
//        double curr = cci.getValue(index).doubleValue();
//        if (prev < -100 && curr >= -100) return Signal.BUY;
//        if (prev > 100 && curr <= 100) return Signal.SELL;
//        return Signal.NONE;
//    }
//}
