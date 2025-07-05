package invest.automate.trade.ml.model;

import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.trees.DecisionStump;
import weka.core.Instance;
import weka.core.Instances;

public class GradientBoostedTradingModel implements TradingMlModel {
    private final AdditiveRegression model = new AdditiveRegression();
    private Instances structure;

    @Override
    public void train(Instances trainingData) throws Exception {
        model.setClassifier(new DecisionStump());
        this.structure = new Instances(trainingData, 0);
        model.buildClassifier(trainingData);
    }

    @Override
    public String predict(Instance instance) throws Exception {
        double label = model.classifyInstance(instance);
        return structure.classAttribute().value((int) label);
    }

    @Override
    public String getName() { return "GradientBoosting"; }
}
