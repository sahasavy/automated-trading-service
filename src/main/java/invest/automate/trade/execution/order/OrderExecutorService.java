package invest.automate.trade.execution.order;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.OrderParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import invest.automate.trade.config.TradingConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExecutorService {

    private final TradingConfig config;
    private final KiteConnect kiteConnect; // inject via ZerodhaKiteService in your service layer

    public void executeOrder(String action, double price, long token, boolean liveMode) {
        int quantity = config.getDefaultOrderQuantity();
        String product = config.getTradeProduct();
        String exchange = config.getTradeExchange();
        String variety = Constants.VARIETY_REGULAR;

        if (!liveMode || config.isPaperTrade()) {
            log.info("[PAPER ORDER] {} {} of token {} at price {}", action, quantity, token, price);
            return;
        }

        try {
            OrderParams params = new OrderParams();
            params.tradingsymbol = getTradingSymbolForToken(token, exchange);
            params.exchange = exchange;
            params.transactionType = action.equalsIgnoreCase("BUY")
                    ? Constants.TRANSACTION_TYPE_BUY : Constants.TRANSACTION_TYPE_SELL;
            params.quantity = quantity;
            params.product = product;
            params.orderType = Constants.ORDER_TYPE_MARKET;
            params.validity = Constants.VALIDITY_DAY;
            String orderId = kiteConnect.placeOrder(params, variety).toString();
            log.info("[LIVE ORDER] {} placed: orderId={}", action, orderId);
        } catch (Exception | KiteException e) {
            log.error("[ORDER ERROR] Could not place live order: {}", e.getMessage(), e);
        }
    }

    private String getTradingSymbolForToken(long token, String exchange) throws Exception, KiteException {
        // Best to cache this for efficiency
        return kiteConnect.getInstruments(exchange).stream()
                .filter(instr -> instr.instrument_token == token)
                .findFirst()
                .orElseThrow(() -> new Exception("Instrument token not found: " + token))
                .tradingsymbol;
    }
}
