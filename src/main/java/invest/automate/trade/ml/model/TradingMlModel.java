package invest.automate.trade.ml.model;

import weka.core.Instance;
import weka.core.Instances;

public interface TradingMlModel {
    void train(Instances trainingData) throws Exception;

    String predict(Instance instance) throws Exception;

    String getName();
}
