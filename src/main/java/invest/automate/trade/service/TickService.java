package invest.automate.trade.service;

import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class TickService {
    public void subscribeTicks(String accessToken, String apiKey, ArrayList<Long> tokens) {
        KiteTicker ticker = new KiteTicker(accessToken, apiKey);
        ticker.setOnConnectedListener(() -> {
            ticker.subscribe(tokens);
            ticker.setMode(tokens, KiteTicker.modeFull);
            log.info("Subscribed to ticks.");
        });
        ticker.setOnTickerArrivalListener(ticks -> {
            for (Tick tick : ticks) {
                log.info("Tick {}: {}", tick.getInstrumentToken(), tick.getLastTradedPrice());
            }
        });
        ticker.connect();
    }
}
