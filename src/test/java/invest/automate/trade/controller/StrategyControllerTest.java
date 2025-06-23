package invest.automate.trade.controller;

import invest.automate.trade.execution.order.OrderExecutor;
import invest.automate.trade.strategy.StrategyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit test for StrategyController.
 */
public class StrategyControllerTest {

    private MockMvc mockMvc;
    private StrategyManager strategyManager;
    private OrderExecutor orderExecutor;

    @BeforeEach
    void setUp() {
        strategyManager = mock(StrategyManager.class);
        orderExecutor = mock(OrderExecutor.class);

        StrategyController controller = new StrategyController(strategyManager, orderExecutor);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testRegisterMovingAvgStrategy() throws Exception {
        String body = "{\"instrument\":\"RELIANCE\",\"shortWindow\":3,\"longWindow\":5}";

        mockMvc.perform(post("/strategies/moving-average")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
