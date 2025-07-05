package invest.automate.trade.ml.model;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;

public class NaiveBayesTradingModel implements TradingMlModel {
    private final NaiveBayes nb = new NaiveBayes();
    private Instances structure;

    @Override
    public void train(Instances trainingData) throws Exception {
        this.structure = new Instances(trainingData, 0);
        nb.buildClassifier(trainingData);
    }

    @Override
    public String predict(Instance instance) throws Exception {
        double label = nb.classifyInstance(instance);
        return structure.classAttribute().value((int) label);
    }

    @Override
    public String getName() { return "NaiveBayes"; }
}
