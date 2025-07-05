package invest.automate.trade.ml.model;

import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;

public class LogisticRegressionTradingModel implements TradingMlModel {
    private final Logistic lr = new Logistic();
    private Instances structure;

    @Override
    public void train(Instances trainingData) throws Exception {
        this.structure = new Instances(trainingData, 0);
        lr.buildClassifier(trainingData);
    }

    @Override
    public String predict(Instance instance) throws Exception {
        double label = lr.classifyInstance(instance);
        return structure.classAttribute().value((int) label);
    }

    @Override
    public String getName() { return "LogisticRegression"; }
}
