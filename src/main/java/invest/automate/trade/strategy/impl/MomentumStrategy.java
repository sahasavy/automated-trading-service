package invest.automate.trade.strategy.impl;

import invest.automate.trade.model.Tick;
import invest.automate.trade.model.OrderType;
import invest.automate.trade.model.SignalType;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.signal.SignalDispatcher;
import invest.automate.trade.strategy.base.Strategy;
import invest.automate.trade.strategy.base.StrategyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * MomentumStrategy detects strong upward or downward movements in price.
 * It generates a BUY signal if the price change exceeds the threshold in positive direction,
 * and a SELL signal if the change exceeds the threshold in negative direction.
 * <p>
 * Config Parameters:
 * - instrument: The instrument token to monitor
 * - threshold: Minimum price change to trigger signal
 * - dispatcher: Dispatcher to send signals
 */
@Slf4j
public class MomentumStrategy implements Strategy {
    private String instrumentToken;
    private double threshold;
    private double lastPrice = -1;
    private SignalDispatcher dispatcher;

    @Override
    public void initialize(StrategyConfig config) {
        Map<String, Object> params = config.getParameters();
        this.instrumentToken = (String) params.get("instrument");
        this.threshold = (double) params.get("threshold");
        this.dispatcher = (SignalDispatcher) params.get("dispatcher");
    }

    @Override
    public void onTick(Tick tick) {
        if (!tick.getInstrumentToken().equals(instrumentToken)) return;

        double price = tick.getLastTradedPrice();
        if (lastPrice < 0) {
            lastPrice = price;
            return;
        }

        double change = price - lastPrice;
        if (Math.abs(change) >= threshold) {
            SignalType type = change > 0 ? SignalType.BUY : SignalType.SELL;
            dispatcher.dispatch(Signal.builder()
                    .instrumentToken(instrumentToken)
                    .type(type)
                    .price(price)
                    .quantity(1)
                    .orderType(OrderType.MARKET)
                    .build());
        }
        lastPrice = price;
    }
}
