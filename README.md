# Automated Trading Service 🧠📈

A plug-and-play, strategy-driven Java microservice for algorithmic trading.  
Built with Spring Boot, Gradle, and designed for Zerodha SDK integration.

### 🔧 Features

- ☑️ Modular strategy engine (plug-in/out strategies easily)
- ☑️ Strategies implemented:
    - Moving Average Crossover
    - Mean Reversion
    - Momentum
- ☑️ Real-time mock tick data simulation
- ☑️ Backtesting engine with CSV input
- ☑️ Signal routing to order executor

### 🛠 Tech Stack

- Java 17
- Spring Boot
- Gradle
- Zerodha SDK (WebSocket + Order API)
- JUnit 5 + Mockito
- JaCoCo for test coverage

---

## 🔧 Configuration

Edit `application.properties`:

```properties
server.port=8092
server.servlet.context-path=/trading-service
```

---

### 📦 Modules

- `backtest`: Tick simulator and backtesting
- `config`: Constants and settings
- `controller`: REST APIs
- `exception`: Global error handling
- `execution.order`: Order placing logic
- `marketdata.websocket`: Real-time data connector
- `model`: Data classes for tick, signal, etc.
- `signal`: Signal generation and dispatch
- `strategy`: Core strategies and base interfaces

---

### Complete Directory Structure

```bash
automated-trading-service/
├── build.gradle
├── README.md
├── settings.gradle
├── src/
│   ├── main/
│   │   ├── java/invest/automate/trade/
│   │   │   ├── AutomatedTradingServiceApplication.java
│   │   │   ├── backtest/
│   │   │   │   ├── BacktestEngine.java
│   │   │   │   └── TickSimulator.java
│   │   │   ├── config/
│   │   │   │   ├── AppConstants.java
│   │   │   │   └── ZerodhaConfig.java
│   │   │   ├── controller/
│   │   │   │   └── StrategyController.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── TradingServiceException.java
│   │   │   ├── execution/order/
│   │   │   │   ├── OrderExecutor.java
│   │   │   │   └── ZerodhaOrderExecutor.java
│   │   │   ├── marketdata/websocket/
│   │   │   │   └── ZerodhaWebSocketClient.java
│   │   │   ├── model/
│   │   │   │   ├── Tick.java
│   │   │   │   ├── SignalType.java
│   │   │   │   └── OrderType.java
│   │   │   ├── signal/
│   │   │   │   ├── Signal.java
│   │   │   │   └── SignalDispatcher.java
│   │   │   ├── strategy/
│   │   │   │   ├── StrategyManager.java
│   │   │   │   ├── base/
│   │   │   │   │   ├── Strategy.java
│   │   │   │   │   └── StrategyConfig.java
│   │   │   │   └── impl/
│   │   │   │       ├── MovingAverageCrossoverStrategy.java
│   │   │   │       ├── MeanReversionStrategy.java
│   │   │   │       └── MomentumStrategy.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/invest/automate/trade/
│       │   ├── AutomatedTradingServiceApplicationTest.java
│       │   ├── controller/
│       │   │   └── StrategyControllerTest.java
│       │   ├── strategy/
│       │   │   ├── MovingAverageCrossoverStrategyTest.java
│       │   │   ├── MeanReversionStrategyTest.java
│       │   │   └── MomentumStrategyTest.java
│       │   └── test/
│       │       ├── TickSimulatorTest.java
│       │       └── BacktestEngineTest.java
│       └── resources/
│           └── sample_ticks.csv
```

---

### 🚀 Run & Build

```bash
./gradlew clean build
./gradlew test
```

---

### 🚀 Test Coverage

```bash
./gradlew jacocoTestReport
```

---

## Maintainers

1. Sourav Saha (sahasavy@gmail.com)
