package invest.automate.trading.runner;

import invest.automate.trading.config.TradingConfig;
import invest.automate.trading.service.BacktestStrategy;
import invest.automate.trading.service.KiteLoginService;
import invest.automate.trading.service.MLModelTrainer;
import invest.automate.trading.service.TickService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppStartupRunner implements CommandLineRunner {

    private final KiteLoginService kiteLoginService;
    private final TickService tickService;
    private final TradingConfig config;
    private final BacktestStrategy backtestStrategy;
    private final MLModelTrainer mlModelTrainer;

    @Override
    public void run(String... args) {
        kiteLoginService.login(config.getApiKey(), config.getApiSecret());

        // Perform Backtest
        backtestStrategy.performBacktest("src/main/resources/historical_data.csv");

        // Train ML Model
        mlModelTrainer.trainModel("src/main/resources/historical_data.csv");

        tickService.subscribeTicks(
                kiteLoginService.getKiteConnect().getAccessToken(),
                config.getApiKey(),
                config.getInstrumentTokens()
        );
    }
}
