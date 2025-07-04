package invest.automate.trade.service;

import com.zerodhatech.models.Tick;
import invest.automate.trade.config.IndicatorConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeriesManagerService {

    private final IndicatorConfig indicatorConfig;

    // Map: instrumentToken -> Map<durationSeconds, BarSeries>
    private final Map<Long, Map<Integer, BarSeries>> allSeries = new HashMap<>();

    // UseCase: Call on each incoming Zerodha tick batch
    public void onTicks(List<Tick> ticks) {
        for (Tick tick : ticks) {
            long token = tick.getInstrumentToken();
            double price = tick.getLastTradedPrice();
            Instant tickTimestamp = (tick.getTickTimestamp() != null) ?
                    tick.getTickTimestamp().toInstant() : Instant.now();
            Instant endTime = tickTimestamp.atZone(ZoneId.systemDefault()).toInstant();

            for (int seconds : indicatorConfig.getBarDurations()) {
                allSeries.computeIfAbsent(token, k -> new HashMap<>())
                        .computeIfAbsent(seconds, s ->
                                new BaseBarSeriesBuilder().withName(token + "_" + seconds + "s").build());

                BarSeries series = allSeries.get(token).get(seconds);

                // UseCase: Add a new bar if time advanced, otherwise update last
                if (series.isEmpty() ||
                        Duration.between(series.getLastBar().getEndTime(), endTime).getSeconds() >= seconds) {
                    // TODO - Check this entire assignments
                    Duration timePeriod = Duration.ofSeconds(seconds);
                    Num openPrice = series.numFactory().numOf(tick.getOpenPrice());
                    Num highPrice = series.numFactory().numOf(tick.getHighPrice());
                    Num lowPrice = series.numFactory().numOf(tick.getLowPrice());
                    Num closePrice = series.numFactory().numOf(tick.getClosePrice());
                    Num volume = series.numFactory().numOf(tick.getVolumeTradedToday());
                    Num amount = series.numFactory().numOf(tick.getLastTradedPrice());
                    long trades = (long) tick.getLastTradedQuantity();

                    Bar bar = new BaseBar(timePeriod, endTime, openPrice, highPrice, lowPrice, closePrice, volume,
                            amount, trades);
                    series.addBar(bar);
                } else {
                    // UseCase: Update last bar (high/low/close/volume) if tick within current bar window
                    Bar lastBar = series.getLastBar();
                    lastBar.addTrade(series.numFactory().numOf(tick.getLastTradedQuantity()),
                            series.numFactory().numOf(price));
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
