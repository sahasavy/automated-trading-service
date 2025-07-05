package invest.automate.trade.ml.model;

import weka.core.Instance;
import weka.core.Instances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MlVotingEnsemble implements TradingMlModel {
    private final List<TradingMlModel> models;
    private Instances structure;

    public MlVotingEnsemble(List<TradingMlModel> models) {
        this.models = models;
    }

    @Override
    public void train(Instances trainingData) throws Exception {
        this.structure = new Instances(trainingData, 0);
        for (TradingMlModel m : models) m.train(trainingData);
    }

    @Override
    public String predict(Instance instance) throws Exception {
        Map<String, Integer> counts = new HashMap<>();
        for (TradingMlModel m : models) {
            String pred = m.predict(instance);
            counts.put(pred, counts.getOrDefault(pred, 0) + 1);
        }
        // Return label with most votes
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NONE");
    }

    @Override
    public String getName() { return "VotingEnsemble"; }
}

