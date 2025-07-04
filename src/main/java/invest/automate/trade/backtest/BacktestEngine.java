package invest.automate.trade.backtest;

import invest.automate.trade.model.SignalType;
import invest.automate.trade.model.Tick;
import invest.automate.trade.signal.Signal;
import invest.automate.trade.model.OrderType;
import invest.automate.trade.strategy.base.Strategy;
import invest.automate.trade.strategy.impl.MeanReversionStrategy;
import invest.automate.trade.strategy.impl.MomentumStrategy;
import invest.automate.trade.strategy.impl.MovingAverageCrossoverStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BacktestEngine {

    /**
     * Loads tick data from the sample_ticks.csv file.
     * Expected CSV format: timestamp, price (and optionally volume or instrument token).
     */
    List<Tick> loadHistoricalTicks() {
        List<Tick> ticks = new ArrayList<>();
        try {
            InputStream is = BacktestEngine.class.getResourceAsStream("/sample_ticks.csv");
            if (is == null) {
                log.error("Historical data file sample_ticks.csv not found.");
                return ticks;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime lastTime = null;
            // Determine if first line is header
            reader.mark(1000);
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return ticks;
            }
            boolean hasHeader = firstLine.toLowerCase().contains("time") || firstLine.toLowerCase().contains("price");
            if (!hasHeader) {
                // reset reader to start if there's no header
                reader.reset();
            }
            // Read data lines
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",\\s*");
                if (fields.length < 2) continue;
                long token = 0;
                LocalDateTime timestamp;
                double price;
                try {
                    if (fields.length == 2) {
                        // Format: timestamp, price
                        timestamp = parseTimestamp(fields[0], formatter);
                        price = Double.parseDouble(fields[1]);
                    } else if (fields.length == 3) {
                        // Possible format: date, time, price OR token, timestamp, price
                        if (fields[0].matches("\\d{4}-\\d{2}-\\d{2}") && fields[1].matches("\\d{2}:\\d{2}:\\d{2}")) {
                            // Format: date, time, price
                            String dateTime = fields[0] + " " + fields[1];
                            timestamp = LocalDateTime.parse(dateTime, formatter);
                            price = Double.parseDouble(fields[2]);
                        } else {
                            // Format: token, timestamp, price
                            token = Long.parseLong(fields[0]);
                            timestamp = parseTimestamp(fields[1], formatter);
                            price = Double.parseDouble(fields[2]);
                        }
                    } else if (fields.length >= 4) {
                        // Format: token, date, time, price
                        token = Long.parseLong(fields[0]);
                        String dateTime = fields[1] + " " + fields[2];
                        timestamp = LocalDateTime.parse(dateTime, formatter);
                        price = Double.parseDouble(fields[3]);
                    } else {
                        continue;
                    }
                } catch (Exception e) {
                    // If parsing fails for timestamp, skip this line
                    log.warn("Skipping line due to parse error: {}", line);
                    continue;
                }
                // If timestamp parsing failed, manufacture one by incrementing lastTime
                if (timestamp == null) {
                    timestamp = (lastTime != null) ? lastTime.plusSeconds(1) : LocalDateTime.now();
                }
                Tick tick = new Tick(token, price, timestamp);
                ticks.add(tick);
                lastTime = timestamp;
            }
            reader.close();
        } catch (Exception e) {
            log.error("Error loading historical ticks: {}", e.getMessage());
        }
        return ticks;
    }

    /**
     * Tries to parse a timestamp string using the given formatter (and a couple of common fallback patterns).
     */
    private LocalDateTime parseTimestamp(String timeStr, DateTimeFormatter baseFormatter) {
        LocalDateTime ts = null;
        try {
            ts = LocalDateTime.parse(timeStr, baseFormatter);
        } catch (Exception e1) {
            // Try ISO date-time
            try {
                ts = LocalDateTime.parse(timeStr);
            } catch (Exception e2) {
                // Could not parse with known patterns
            }
        }
        return ts;
    }

    /**
     * Runs a backtest simulation on the given strategy with the provided historical tick data.
     * @param strategy the trading strategy to test
     * @param history the list of historical ticks
     */
    public void runBacktest(Strategy strategy, List<Tick> history) {
        double startingCapital = 100000.0;
        double cash = startingCapital;
        double positionEntryPrice = 0.0;
        boolean positionOpen = false;
        OrderType positionType = null;  // track if current open position is BUY (long) or SELL (short)
        int trades = 0;
        int wins = 0;
        double totalProfit = 0.0;

        for (Tick tick : history) {
            Signal signal = strategy.generateSignal(tick);
            if (signal != null) {
                OrderType side = signal.getOrderType();
                SignalType type = signal.getSignalType();
                double price = signal.getPrice();
                if (type == SignalType.ENTRY && !positionOpen) {
                    // Enter a new position
                    positionOpen = true;
                    positionType = side;
                    positionEntryPrice = price;
                    trades++;
                    log.debug("Entered {} position at {}", side, price);
                } else if (type == SignalType.EXIT && positionOpen && positionType != null) {
                    // Exit the existing position
                    double profit = 0.0;
                    if (positionType == OrderType.BUY) {
                        // Long position profit = sell price - buy price
                        profit = price - positionEntryPrice;
                    } else if (positionType == OrderType.SELL) {
                        // Short position profit = entry price - cover(buy) price
                        profit = positionEntryPrice - price;
                    }
                    cash += profit;  // update cash with profit/loss
                    totalProfit += profit;
                    if (profit > 0.0001) {
                        wins++;
                    }
                    positionOpen = false;
                    positionType = null;
                    log.debug("Exited position at {} with P/L = {}", price, profit);
                }
            }
        }
        // If a position is still open at the end, close it at the last price
        if (positionOpen && positionType != null) {
            Tick lastTick = history.get(history.size() - 1);
            double lastPrice = lastTick.getPrice();
            double profit = 0.0;
            if (positionType == OrderType.BUY) {
                profit = lastPrice - positionEntryPrice;
            } else if (positionType == OrderType.SELL) {
                profit = positionEntryPrice - lastPrice;
            }
            cash += profit;
            totalProfit += profit;
            if (profit > 0.0001) {
                wins++;
            }
            trades++;
            log.debug("Closed final open position at end price {} with P/L = {}", lastPrice, profit);
        }

        // Calculate win rate and final metrics
        double winRate = (trades > 0) ? (wins * 100.0 / trades) : 0.0;
        double netProfit = cash - startingCapital;
        log.info("Backtest results for {}:", strategy.getClass().getSimpleName());
        log.info("Initial Capital: {}", startingCapital);
        log.info("Final Capital: {}", cash);
        log.info("Net Profit: {}", netProfit);
        log.info("Total Trades: {}", trades);
        log.info("Winning Trades: {}", wins);
        log.info("Win Rate: {:.2f}%".replace("{:.2f}", String.format("%.2f", winRate)));
    }

    public static void main(String[] args) {
        BacktestEngine engine = new BacktestEngine();
        List<Tick> history = engine.loadHistoricalTicks();
        if (history.isEmpty()) {
            System.err.println("No historical data available for backtest.");
            return;
        }
        // Instantiate strategies to test
        Strategy meanRev = new MeanReversionStrategy();
        Strategy momentum = new MomentumStrategy();
        Strategy maCross = new MovingAverageCrossoverStrategy();
        // Run backtests for each strategy
        engine.runBacktest(meanRev, history);
        engine.runBacktest(momentum, history);
        engine.runBacktest(maCross, history);
    }
}
