package invest.automate.trade.strategy.impl;

import invest.automate.trade.model.OrderType;
import invest.automate.trade.model.SignalType;
import invest.automate.trade.model.Tick;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.signal.SignalDispatcher;
import invest.automate.trade.strategy.base.Strategy;
import invest.automate.trade.strategy.base.StrategyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Implements the classic moving average crossover strategy.
 * When short-term average crosses above long-term -> BUY.
 * When short-term average crosses below long-term -> SELL.
 */
@Slf4j
public class MovingAverageCrossoverStrategy implements Strategy {
    private String instrumentToken;
    private int shortWindow;
    private int longWindow;
    private SignalDispatcher dispatcher;

    private final Queue<Double> shortTerm = new LinkedList<>();
    private final Queue<Double> longTerm = new LinkedList<>();

    @Override
    public void initialize(StrategyConfig config) {
        Map<String, Object> params = config.getParameters();
        this.instrumentToken = (String) params.get("instrument");
        this.shortWindow = (int) params.get("shortWindow");
        this.longWindow = (int) params.get("longWindow");
        this.dispatcher = (SignalDispatcher) params.get("dispatcher");
    }

    @Override
    public void onTick(Tick tick) {
        if (!tick.getInstrumentToken().equals(instrumentToken)) return;

        double price = tick.getLastTradedPrice();
        shortTerm.add(price);
        longTerm.add(price);

        if (shortTerm.size() > shortWindow) shortTerm.poll();
        if (longTerm.size() > longWindow) longTerm.poll();

        if (shortTerm.size() == shortWindow && longTerm.size() == longWindow) {
            double shortAvg = shortTerm.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double longAvg = longTerm.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            if (shortAvg > longAvg) {
                dispatcher.dispatch(Signal.builder()
                        .instrumentToken(instrumentToken)
                        .type(SignalType.BUY)
                        .price(price)
                        .quantity(1)
                        .orderType(OrderType.MARKET)
                        .build());
            } else if (shortAvg < longAvg) {
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
