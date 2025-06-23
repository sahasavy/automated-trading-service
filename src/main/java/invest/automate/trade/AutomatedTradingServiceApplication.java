package invest.automate.trade;

import invest.automate.trade.execution.order.OrderExecutor;
import invest.automate.trade.execution.order.ZerodhaOrderExecutor;
import invest.automate.trade.marketdata.websocket.ZerodhaWebSocketClient;
import invest.automate.trade.strategy.StrategyManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main application bootstrapper for the Automated Trading Service.
 */
@SpringBootApplication
public class AutomatedTradingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomatedTradingServiceApplication.class, args);
    }

    @Bean
    public StrategyManager strategyManager() {
        return new StrategyManager();
    }

    @Bean
    public OrderExecutor orderExecutor() {
        return new ZerodhaOrderExecutor();
    }

    @Bean
    public ZerodhaWebSocketClient webSocketClient(StrategyManager strategyManager) {
        ZerodhaWebSocketClient client = new ZerodhaWebSocketClient(strategyManager);
        client.connect(); // Start mock streaming on app boot
        return client;
    }
}
