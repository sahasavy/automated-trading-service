package invest.automate.trade.ml;

import invest.automate.trade.ml.model.TradingMlModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import weka.core.Instance;
import weka.core.Instances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MlModelService {

    // All models registered (inject with @Autowired or @RequiredArgsConstructor)
    private final List<TradingMlModel> mlModels;

    // Keep track of which models are trained
    private final Map<String, Boolean> trained = new HashMap<>();

    // Train all models (or select which ones based on config)
    public void trainAll(Instances trainingData) throws Exception {
        for (TradingMlModel model : mlModels) {
            model.train(trainingData);
            trained.put(model.getName(), true);
        }
    }

    // Predict using a specific model (by name)
    public String predict(String modelName, Instance instance) throws Exception {
        for (TradingMlModel model : mlModels) {
            if (model.getName().equalsIgnoreCase(modelName) && trained.getOrDefault(model.getName(), false)) {
                return model.predict(instance);
            }
        }
        throw new IllegalArgumentException("Model not found or not trained: " + modelName);
    }

    // Predict using all models (for ensemble)
    public Map<String, String> predictAll(Instance instance) throws Exception {
        Map<String, String> results = new HashMap<>();
        for (TradingMlModel model : mlModels) {
            if (trained.getOrDefault(model.getName(), false)) {
                results.put(model.getName(), model.predict(instance));
            }
        }
        return results;
    }
}
