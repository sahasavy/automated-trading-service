# Automated Trading Service 🧠📈

A plug-and-play, strategy-driven Java microservice for algorithmic trading.  
Built with Spring Boot, Gradle, and designed for Zerodha SDK integration.

### 🛠 Tech Stack

- Java 21
- Spring Boot
- Gradle
- Zerodha SDK (WebSocket + Order API)
- JUnit 5 + Mockito
- JaCoCo for test coverage

---

## 🔧 Configuration

Edit `application.yml`:

```properties
server.port=8092
server.servlet.context-path=/trading-service
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
