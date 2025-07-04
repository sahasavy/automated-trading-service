package invest.automate.trade.controller;

import invest.automate.trade.model.requests.LiveTradeRequest;
import invest.automate.trade.service.LiveTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveTradeController {

    private final LiveTradeService liveTradeService;

    @PostMapping("/start")
    public ResponseEntity<?> startLiveTrading(@RequestBody(required = false) LiveTradeRequest request) {
        liveTradeService.startLiveTrading(request);
        return ResponseEntity.ok("Live trading started");
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopLiveTrading() {
        liveTradeService.stopLiveTrading();
        return ResponseEntity.ok("Live trading stopped");
    }
}
