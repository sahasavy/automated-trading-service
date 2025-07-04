package invest.automate.trade.service;

import com.zerodhatech.models.Tick;
import invest.automate.trade.config.TradingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeriesManagerService {

    private final TradingConfig config;

    // Map: instrumentToken -> Map<durationSeconds, BarSeries>
    private final Map<Long, Map<Integer, BarSeries>> allSeries = new HashMap<>();

    // UseCase: Call on each incoming Zerodha tick batch
    public void onTicks(List<Tick> ticks) {
        for (Tick tick : ticks) {
            long token = tick.getInstrumentToken();
            double price = tick.getLastTradedPrice();
            Instant ts = (tick.getTickTimestamp() != null) ? tick.getTickTimestamp().toInstant() : Instant.now();
            ZonedDateTime dateTime = ts.atZone(ZoneId.systemDefault());

            for (int seconds : config.getBarDurations()) {
                allSeries.computeIfAbsent(token, k -> new HashMap<>())
                        .computeIfAbsent(seconds, s ->
                                new BaseBarSeriesBuilder().withName(token + "_" + seconds + "s").build());

                BarSeries series = allSeries.get(token).get(seconds);

                // UseCase: Add a new bar if time advanced, otherwise update last
                if (series.isEmpty() ||
                        Duration.between(series.getLastBar().getEndTime(), dateTime).getSeconds() >= seconds) {
                    Bar bar = BaseBar.builder()
                            .timePeriod(Duration.ofSeconds(seconds))
                            .endTime(dateTime)
                            .openPrice(price)
                            .highPrice(price)
                            .lowPrice(price)
                            .closePrice(price)
                            .volume(tick.getLastTradedQuantity())
                            .build();
                    series.addBar(bar);
                } else {
                    // UseCase: Update last bar (high/low/close/volume) if tick within current bar window
                    Bar lastBar = series.getLastBar();
                    lastBar.addTrade(tick.getLastTradedQuantity(), price);
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
