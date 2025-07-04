package invest.automate.trade.service.broker;

import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import invest.automate.trade.config.ZerodhaConfig;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZerodhaKiteService {

    private final ZerodhaConfig zerodhaConfig;

    @Getter
    private KiteConnect kiteConnect;

    private void initializeKiteConnect() {
        try {
            kiteConnect = new KiteConnect(zerodhaConfig.getKiteApiKey(), true);
            kiteConnect.setUserId(zerodhaConfig.getAccountUserId());

            //TODO - Interactive login required to generate requestToken and accessToken:
            String url = kiteConnect.getLoginURL();
            log.info("Kite Login URL : %s", url);
            // Output : https://kite.zerodha.com/connect/login?v=3&api_key=bhvduzgqkyhxfo7i

            // Set session expiry callback.
            kiteConnect.setSessionExpiryHook(() -> log.info("Session Expired"));

            log.info("KiteConnect API client created successfully");
        } catch (Exception e) {
            log.error("Failed to initialize KiteConnect: {}", e.getMessage(), e);
        }
    }

    /**
     * The request token can to be obtained after completion of login process.
     * <br>
     * Check out https://kite.trade/docs/connect/v3/user/#login-flow for more information.
     * <br>
     * <p>
     * A request token is valid for only a couple of minutes and can be used only once.
     * An access token is valid for one whole day. Don't call this method for every app run.
     * </p>
     * <br>
     * Once an access token is received it should be stored in preferences or database for further usage.
     *
     * @throws IOException
     */
    private void setTokens() {
        try {
            String accessToken = zerodhaConfig.getKiteAccessToken();
            String publicToken = zerodhaConfig.getKitePublicToken();

            if (StringUtils.isBlank(accessToken)) {
                log.info("Login for the 1st time during the day");

                User user = kiteConnect.generateSession(zerodhaConfig.getKiteRequestToken(), zerodhaConfig.getKiteApiSecret());

                accessToken = user.accessToken;
                publicToken = user.publicToken;

                log.info("Access Token : %s", accessToken);
                log.info("Public Token : %s", publicToken);
            }

            kiteConnect.setAccessToken(accessToken);
            kiteConnect.setPublicToken(publicToken);
        } catch (KiteException e) {
            log.error("KiteException occurred : " + e.getMessage());
        } catch (IOException e) {
            log.error("IOException occurred : " + e.getMessage());
        }
    }

    public void loginToKite() {
        initializeKiteConnect();
        setTokens();
    }
}
