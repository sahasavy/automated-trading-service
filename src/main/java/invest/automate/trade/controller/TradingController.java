package invest.automate.trade.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradingController {

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        // Return status from a service (TODO - to be implemented)
        return ResponseEntity.ok("Status endpoint not implemented yet.");
    }
}
