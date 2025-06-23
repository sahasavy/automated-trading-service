package invest.automate.trade.strategy.impl;

import invest.automate.trade.model.Tick;
import invest.automate.trade.model.OrderType;
import invest.automate.trade.model.SignalType;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.signal.SignalDispatcher;
import invest.automate.trade.strategy.base.Strategy;
import invest.automate.trade.strategy.base.StrategyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * MeanReversionStrategy generates BUY signals when the price drops below the average by a given threshold
 * and generates SELL signals when the price exceeds the average by the threshold.
 * <p>
 * Config Parameters:
 * - instrument: The instrument token to monitor
 * - window: The number of previous prices to use in average calculation
 * - threshold: The deviation threshold to trigger a trade
 * - dispatcher: Dispatcher to send signals
 */
@Slf4j
public class MeanReversionStrategy implements Strategy {
    private String instrumentToken;
    private int window;
    private double threshold;
    private SignalDispatcher dispatcher;
    private final Queue<Double> prices = new LinkedList<>();

    @Override
    public void initialize(StrategyConfig config) {
        Map<String, Object> params = config.getParameters();
        this.instrumentToken = (String) params.get("instrument");
        this.window = (int) params.get("window");
        this.threshold = (double) params.get("threshold");
        this.dispatcher = (SignalDispatcher) params.get("dispatcher");
    }

    @Override
    public void onTick(Tick tick) {
        if (!tick.getInstrumentToken().equals(instrumentToken)) return;

        double price = tick.getLastTradedPrice();
        prices.add(price);
        if (prices.size() > window) prices.poll();

        if (prices.size() == window) {
            double avg = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            if (price < avg - threshold) {
                dispatcher.dispatch(Signal.builder()
                        .instrumentToken(instrumentToken)
                        .type(SignalType.BUY)
                        .price(price)
                        .quantity(1)
                        .orderType(OrderType.MARKET)
                        .build());
            } else if (price > avg + threshold) {
                dispatcher.dispatch(Signal.builder()
                        .instrumentToken(instrumentToken)
                        .type(SignalType.SELL)
                        .price(price)
                        .quantity(1)
                        .orderType(OrderType.MARKET)
                        .build());
            }
        }
    }
}
