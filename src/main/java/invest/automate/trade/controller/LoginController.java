package invest.automate.trade.controller;

import invest.automate.trade.service.broker.ZerodhaKiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final ZerodhaKiteService zerodhaKiteService;

    @GetMapping("/login")
    public ResponseEntity<?> login() {
        // TODO - to be implemented
        zerodhaKiteService.loginToKite();

        return ResponseEntity.ok("Login Successful");
    }
}
