package invest.automate.trade.controller;

import invest.automate.trade.model.requests.PaperTradeRequest;
import invest.automate.trade.service.PaperTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paper")
@RequiredArgsConstructor
public class PaperTradeController {

    private final PaperTradeService paperTradeService;

    @PostMapping("/start")
    public ResponseEntity<?> startPaperTrading(@RequestBody(required = false) PaperTradeRequest request) {
        paperTradeService.startPaperTrading(request);
        return ResponseEntity.ok("Paper trading started");
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopPaperTrading() {
        paperTradeService.stopPaperTrading();
        return ResponseEntity.ok("Paper trading stopped");
    }
}
