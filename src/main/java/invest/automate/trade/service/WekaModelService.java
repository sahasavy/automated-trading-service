package invest.automate.trade.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@Service
public class WekaModelService {

    private Classifier model;
    private Instances dataFormat;

    /**
     * Train the model from a list of prices (or features); this is a simple illustration.
     */
    public void trainModel(BarSeries series) throws Exception {
        // Attributes: [price, rsi], Class: [UP, DOWN]
        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("price"));
        attrs.add(new Attribute("rsi"));
        ArrayList<String> classVals = new ArrayList<>(Arrays.asList("UP", "DOWN"));
        attrs.add(new Attribute("class", classVals));
        Instances dataset = new Instances("TickFeatures", attrs, 0);
        dataset.setClassIndex(2);

        // Build features from series
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);
        for (int i = 1; i < series.getBarCount() - 1; i++) {
            double price = close.getValue(i).doubleValue();
            double rsiVal = rsi.getValue(i).doubleValue();
            double nextPrice = close.getValue(i+1).doubleValue();
            String direction = nextPrice > price ? "UP" : "DOWN";
            Instance instance = new DenseInstance(3);
            instance.setValue(attrs.get(0), price);
            instance.setValue(attrs.get(1), rsiVal);
            instance.setValue(attrs.get(2), direction);
            dataset.add(instance);
        }

        dataFormat = dataset.stringFreeStructure();
        model = new RandomForest();
        model.buildClassifier(dataset);
        log.info("Trained Weka RandomForest model with {} instances", dataset.size());
    }

    /**
     * Predict using current tick features (returns "UP" or "DOWN").
     */
    public String predict(BarSeries series) throws Exception {
        if (model == null || dataFormat == null || series.getBarCount() < 15) return "NONE";
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, 14);
        int i = series.getEndIndex();
        double price = close.getValue(i).doubleValue();
        double rsiVal = rsi.getValue(i).doubleValue();
        Instance instance = new DenseInstance(3);
        instance.setValue(dataFormat.attribute(0), price);
        instance.setValue(dataFormat.attribute(1), rsiVal);
        instance.setDataset(dataFormat);
        double predIndex = model.classifyInstance(instance);
        return dataFormat.classAttribute().value((int) predIndex);
    }
}
