package invest.automate.trade.ml.config;

import invest.automate.trade.ml.model.*;
import invest.automate.trade.ml.model.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MlModelConfig {
    @Bean
    public List<TradingMlModel> mlModels() {
        return List.of(
                new RandomForestTradingModel(),
                new GradientBoostedTradingModel(),
                new SvmTradingModel(),
                new LogisticRegressionTradingModel(),
                new NaiveBayesTradingModel()
        );
    }
}
