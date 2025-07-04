package invest.automate.trading.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.criteria.pnl.ReturnCriterion;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BacktestStrategy {

    private final IndicatorCalculation indicatorCalculation;

    public void performBacktest(String historicalDataCsv) {
        BarSeries series = loadHistoricalData(historicalDataCsv);
        Strategy strategy = indicatorCalculation.buildEmaCrossoverStrategy(series);

        BarSeriesManager manager = new BarSeriesManager(series);
        TradingRecord record = manager.run(strategy);

        ReturnCriterion criterion = new ReturnCriterion();
        Num totalReturn = criterion.calculate(series, record);

        log.info("Total Trades: {}", record.getTrades().size());
        log.info("Total Return: {}", totalReturn);
    }

    private BarSeries loadHistoricalData(String csvFile) {
        BarSeries series = new BaseBarSeriesBuilder().withName("historical_series").build();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                Duration timePeriod = java.time.Duration.ofDays(1);
                LocalDate localDate = LocalDate.parse(cols[0], df);
                Instant endTime = localDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();

                DoubleNum openPrice = DoubleNum.valueOf(Double.parseDouble(cols[1]));
                DoubleNum highPrice = DoubleNum.valueOf(Double.parseDouble(cols[2]));
                DoubleNum lowPrice = DoubleNum.valueOf(Double.parseDouble(cols[3]));
                DoubleNum closePrice = DoubleNum.valueOf(Double.parseDouble(cols[4]));
                DoubleNum volume = DoubleNum.valueOf(Double.parseDouble(cols[5]));
                DoubleNum amount = DoubleNum.valueOf(123); //TODO - What to do with this?
                long trades = 123; //TODO - What to do with this?

                // Use BarBuilder to create a Bar and add to the series
                Bar bar = new BaseBar(timePeriod, endTime, openPrice, highPrice, lowPrice, closePrice, volume, amount, trades);

                series.addBar(bar);
            }
            log.info("Loaded data from {}", csvFile);
        } catch (Exception e) {
            log.error("Error loading CSV: {}", e.getMessage(), e);
        }
        return series;
    }
}
