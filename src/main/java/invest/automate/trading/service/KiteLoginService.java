package invest.automate.trading.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Getter
@Slf4j
@Service
public class KiteLoginService {

    private KiteConnect kiteConnect;

    public void login(String apiKey, String apiSecret) {
        kiteConnect = new KiteConnect(apiKey);
        try (Scanner sc = new Scanner(System.in)) {
            log.info("Login at: https://kite.trade/connect/login?api_key={}", apiKey);
            log.info("Enter Request Token: ");
            String requestToken = sc.nextLine();
            User user = kiteConnect.generateSession(requestToken, apiSecret);
            kiteConnect.setAccessToken(user.accessToken);
            log.info("Login successful.");
        } catch (Exception | KiteException e) {
            log.error("Login error: {}", e.getMessage(), e);
        }
    }
}
