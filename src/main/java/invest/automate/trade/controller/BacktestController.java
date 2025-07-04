package invest.automate.trade.controller;

import invest.automate.trade.model.requests.BacktestRequest;
import invest.automate.trade.backtest.BacktestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backtest")
@RequiredArgsConstructor
public class BacktestController {

    private final BacktestService backtestService;

    @PostMapping("/start")
    public ResponseEntity<?> runBacktest(@RequestBody(required = false) BacktestRequest request) {
        var result = backtestService.runBacktest();
        return ResponseEntity.ok(result);
    }
}
