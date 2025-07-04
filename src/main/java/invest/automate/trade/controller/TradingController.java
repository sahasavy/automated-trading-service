package invest.automate.trade.controller;

import invest.automate.trade.service.BacktestService;
import invest.automate.trade.service.PaperTradeService;
import invest.automate.trade.service.LiveTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradingController {

    private final BacktestService backtestService;
    private final PaperTradeService paperTradeService;
    private final LiveTradeService liveTradeService;

    // Trigger a backtest. You can extend the BacktestRequest to allow more control.
    @PostMapping("/backtest")
    public ResponseEntity<?> runBacktest(@RequestBody(required = false) BacktestRequest request) {
        var result = backtestService.runBacktest(request);
        return ResponseEntity.ok(result);
    }

    // Start/Stop paper trading (non-blocking, status tracked in service)
    @PostMapping("/paper/start")
    public ResponseEntity<?> startPaperTrading(@RequestBody(required = false) PaperTradeRequest request) {
        paperTradeService.startPaperTrading(request);
        return ResponseEntity.ok("Paper trading started");
    }
    @PostMapping("/paper/stop")
    public ResponseEntity<?> stopPaperTrading() {
        paperTradeService.stopPaperTrading();
        return ResponseEntity.ok("Paper trading stopped");
    }

    // Start/Stop live trading
    @PostMapping("/live/start")
    public ResponseEntity<?> startLiveTrading(@RequestBody(required = false) LiveTradeRequest request) {
        liveTradeService.startLiveTrading(request);
        return ResponseEntity.ok("Live trading started");
    }
    @PostMapping("/live/stop")
    public ResponseEntity<?> stopLiveTrading() {
        liveTradeService.stopLiveTrading();
        return ResponseEntity.ok("Live trading stopped");
    }

    // Get current trading status (optional extension)
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        // Return status from a service (to be implemented)
        return ResponseEntity.ok("Status endpoint not implemented yet.");
    }
}
