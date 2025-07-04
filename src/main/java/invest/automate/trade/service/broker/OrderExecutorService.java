package invest.automate.trade.service.broker;

import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.OrderParams;
import invest.automate.trade.config.LiveConfig;
import invest.automate.trade.config.PaperConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExecutorService {

    private final PaperConfig paperConfig;
    private final LiveConfig liveConfig;
    private final ZerodhaKiteService zerodhaKiteService;

    public void executeOrder(String action, double price, long token, boolean liveMode) {
        int quantity = liveConfig.getDefaultOrderQuantity();
        String product = liveConfig.getTradeProduct();
        String exchange = liveConfig.getTradeExchange();
        String variety = Constants.VARIETY_REGULAR;

        if (!liveMode || paperConfig.isPaperTrade()) {
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
            String orderId = zerodhaKiteService.getKiteConnect().placeOrder(params, variety).toString();
            log.info("[LIVE ORDER] {} placed: orderId={}", action, orderId);
        } catch (Exception | KiteException e) {
            log.error("[ORDER ERROR] Could not place live order: {}", e.getMessage(), e);
        }
    }

    private String getTradingSymbolForToken(long token, String exchange) throws Exception, KiteException {
        // TODO - Need to cache this for efficiency
        return zerodhaKiteService.getKiteConnect()
                .getInstruments(exchange)
                .stream()
                .filter(instr -> instr.instrument_token == token)
                .findFirst()
                .orElseThrow(() -> new Exception("Instrument token not found: " + token))
                .tradingsymbol;
    }
}
