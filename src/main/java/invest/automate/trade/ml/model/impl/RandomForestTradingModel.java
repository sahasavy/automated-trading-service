package invest.automate.trade.ml.model.impl;

import invest.automate.trade.ml.model.TradingMlModel;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

public class RandomForestTradingModel implements TradingMlModel {
    private final RandomForest rf = new RandomForest();
    private Instances structure;

    @Override
    public void train(Instances trainingData) throws Exception {
        this.structure = new Instances(trainingData, 0);
        rf.buildClassifier(trainingData);
    }

    @Override
    public String predict(Instance instance) throws Exception {
        double label = rf.classifyInstance(instance);
        return structure.classAttribute().value((int) label); // e.g. "UP", "DOWN", "NONE"
    }

    @Override
    public String getName() { return "RandomForest"; }
}
