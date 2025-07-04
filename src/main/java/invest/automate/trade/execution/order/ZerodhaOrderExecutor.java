package invest.automate.trade.execution.order;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Instrument;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.model.OrderType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ZerodhaOrderExecutor implements OrderExecutor {

    private KiteConnect kiteConnect;
    private boolean paperMode;
    private int defaultQuantity;

    public ZerodhaOrderExecutor() {
        // Load API credentials and configure KiteConnect for live trading if needed
        String apiKey = System.getenv("KITE_API_KEY");
        String accessToken = System.getenv("KITE_ACCESS_TOKEN");
        String userId = System.getenv("KITE_USER_ID");
        if (apiKey == null || accessToken == null) {
            throw new IllegalStateException("Kite API credentials not set for OrderExecutor.");
        }
        this.kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setAccessToken(accessToken);
        if (userId != null) {
            kiteConnect.setUserId(userId);
        }
        // Determine mode (default to paper trading)
        String paperFlag = System.getenv("PAPER_TRADING");
        this.paperMode = paperFlag == null || !paperFlag.equalsIgnoreCase("false");
        // Determine order quantity (default 1)
        String qtyStr = System.getenv("TRADE_QUANTITY");
        this.defaultQuantity = (qtyStr != null) ? Integer.parseInt(qtyStr) : 1;
    }

    @Override
    public void executeOrder(Signal signal) {
        OrderType side = signal.getOrderType();
        long token = signal.getInstrumentToken();
        double price = signal.getPrice();
        if (paperMode) {
            // Paper trading: just log the action without placing a real order
            log.info("[Paper Trade] {} {} units of instrument {} at price {}",
                    side, defaultQuantity, token, price);
        } else {
            try {
                // Live trading: place a market order via Kite Connect API
                OrderParams orderParams = new OrderParams();
                // Map our OrderType to Kite transaction type
                orderParams.transactionType = (side == OrderType.BUY)
                        ? Constants.TRANSACTION_TYPE_BUY
                        : Constants.TRANSACTION_TYPE_SELL;
                // For simplicity, we use exchange NSE and product MIS (intraday) as defaults
                orderParams.exchange = Constants.EXCHANGE_NSE;
                // We need the tradingsymbol for the token
                String tradingSymbol = getTradingSymbolForToken(token);
                orderParams.tradingsymbol = tradingSymbol;
                orderParams.product = Constants.PRODUCT_MIS;
                orderParams.orderType = Constants.ORDER_TYPE_MARKET;
                orderParams.quantity = defaultQuantity;
                orderParams.validity = Constants.VALIDITY_DAY;
                // Place regular order
                kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
                log.info("Live order placed: {} {} of {} (token {}) at market price.",
                        side, defaultQuantity, tradingSymbol, token);
            } catch (KiteException | Exception e) {
                log.error("Order placement failed: {}", e.getMessage());
            }
        }
    }

    /**
     * Utility to retrieve the trading symbol for a given instrument token using KiteConnect.
     * This method fetches the instrument list and finds the matching token.
     */
    private String getTradingSymbolForToken(long token) throws KiteException, IOException {
        // Fetch all instruments for NSE and search (this is a simplified approach)
        return kiteConnect.getInstruments("NSE").stream()
                .filter(instr -> instr.getInstrument_token() == token)
                .findFirst()
                .map(Instrument::getTradingsymbol)
                .orElseThrow(() -> new KiteException("Instrument token " + token + " not found"));
    }
}
