package invest.automate.trade.service;

import com.zerodhatech.models.Tick;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeriesManagerService {

    private final TradingConfig config;

    // Map: instrumentToken -> Map<durationSeconds, BarSeries>
    private final Map<Long, Map<Integer, BarSeries>> allSeries = new HashMap<>();

    // Call on each incoming Zerodha tick batch
    public void onTicks(List<Tick> ticks) {
        for (Tick tick : ticks) {
            long token = tick.instrumentToken;
            double price = tick.lastTradedPrice;
            Instant ts = (tick.timestamp != null) ? tick.timestamp.toInstant() : Instant.now();
            ZonedDateTime dateTime = ts.atZone(ZoneId.systemDefault());

            for (int seconds : config.getBarDurations()) {
                allSeries
                        .computeIfAbsent(token, k -> new HashMap<>())
                        .computeIfAbsent(seconds, s -> new BaseBarSeriesBuilder().withName(token + "_" + seconds + "s").build());

                BarSeries series = allSeries.get(token).get(seconds);

                // Add a new bar if time advanced, otherwise update last
                if (series.isEmpty() ||
                        Duration.between(series.getLastBar().getEndTime(), dateTime).getSeconds() >= seconds) {
                    Bar bar = BaseBar.builder()
                            .timePeriod(Duration.ofSeconds(seconds))
                            .endTime(dateTime)
                            .openPrice(price)
                            .highPrice(price)
                            .lowPrice(price)
                            .closePrice(price)
                            .volume(tick.lastTradedQuantity)
                            .build();
                    series.addBar(bar);
                } else {
                    // Update last bar (high/low/close/volume) if tick within current bar window
                    Bar lastBar = series.getLastBar();
                    lastBar.addTrade(tick.lastTradedQuantity, price);
                }
            }
        }
    }

    // For indicators/strategies
    public BarSeries getSeries(long instrumentToken, int duration) {
        return Optional.ofNullable(allSeries.get(instrumentToken))
                .map(map -> map.get(duration))
                .orElse(null);
    }
}
