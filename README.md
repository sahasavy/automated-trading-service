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

- `model`: Data classes for tick, signal, etc.
- `strategy`: Core strategies and base interfaces
- `signal`: Signal generation and dispatch
- `execution.order`: Order placing logic
- `marketdata.websocket`: Real-time data connector
- `backtest`: Tick simulator and backtesting
- `controller`: REST APIs
- `config`: Constants and settings
- `exception`: Global error handling

---

### Complete Directory Structure

```bash
automated-trading-service/
├── README.md
├── build.gradle
├── settings.gradle
├── src/
│   ├── main/
│   │   ├── java/invest/automate/trade/
│   │   │   ├── model/
│   │   │   │   ├── Tick.java
│   │   │   │   ├── SignalType.java
│   │   │   │   ├── OrderType.java
│   │   │   ├── signal/
│   │   │   │   ├── Signal.java
│   │   │   │   ├── SignalDispatcher.java
│   │   │   ├── strategy/
│   │   │   │   ├── StrategyManager.java
│   │   │   │   ├── base/
│   │   │   │   │   ├── Strategy.java
│   │   │   │   │   └── StrategyConfig.java
│   │   │   │   └── impl/
│   │   │   │       └── MovingAverageCrossoverStrategy.java
│   │   │   ├── execution/order/
│   │   │   │   ├── OrderExecutor.java
│   │   │   │   └── ZerodhaOrderExecutor.java
│   │   │   ├── marketdata/websocket/
│   │   │   │   └── ZerodhaWebSocketClient.java
│   │   │   ├── backtest/
│   │   │   │   ├── TickSimulator.java
│   │   │   │   └── BacktestEngine.java
│   │   │   ├── controller/
│   │   │   │   └── StrategyController.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── TradingServiceException.java
│   │   │   └── config/
│   │   │       └── AppConstants.java
│   │   └── resources/
│   │       ├── application.properties
│   └── test/
│       ├── java/invest/automate/trade/
│       │   ├── controller/
│       │   │   └── StrategyControllerTest.java
│       │   ├── strategy/
│       │   │   └── MovingAverageCrossoverStrategyTest.java
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
