package invest.automate.trade.ml.model;

import weka.classifiers.functions.SMO;
import weka.core.Instance;
import weka.core.Instances;

public class SvmTradingModel implements TradingMlModel {
    private final SMO svm = new SMO();
    private Instances structure;

    @Override
    public void train(Instances trainingData) throws Exception {
        this.structure = new Instances(trainingData, 0);
        svm.buildClassifier(trainingData);
    }

    @Override
    public String predict(Instance instance) throws Exception {
        double label = svm.classifyInstance(instance);
        return structure.classAttribute().value((int) label);
    }

    @Override
    public String getName() { return "SVM"; }
}
