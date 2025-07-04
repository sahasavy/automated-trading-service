package invest.automate.trade.backtest;

import invest.automate.trade.model.Tick;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class WekaModelTrainer {

    /**
     * Prepares a Weka Instances dataset from historical ticks with features:
     * - Price difference from previous tick
     * - RSI value (period 14)
     * Class label: "UP" if next tick price is higher, "DOWN" if next tick price is lower.
     */
    private Instances prepareDataset(ArrayList<Tick> ticks) {
        // Define attributes
        Attribute attrPriceDiff = new Attribute("PriceDiff");
        Attribute attrRSI = new Attribute("RSI");
        // Class attribute (nominal)
        ArrayList<String> classValues = new ArrayList<>(Arrays.asList("DOWN", "UP"));
        Attribute attrClass = new Attribute("Direction", classValues);
        // Create dataset structure
        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(attrPriceDiff);
        attrs.add(attrRSI);
        attrs.add(attrClass);
        Instances data = new Instances("TickData", attrs, 0);
        data.setClassIndex(data.numAttributes() - 1);

        // Build a TA4J series to compute RSI
        BarSeries series = new BaseBarSeriesBuilder().withName("WekaSeries").build();
        ClosePriceIndicator closeIndicator = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closeIndicator, 14);

        // Add bars to series and collect features
        // We'll skip the first tick (no previous diff) and last tick (no next price for label)
        for (int i = 0; i < ticks.size(); i++) {
            // Add tick as a bar for RSI calculation
            Tick tick = ticks.get(i);
            BacktestEngine dummyEngine = new BacktestEngine();
            // Reuse BacktestEngine's method to add bar (could be refactored for reuse)
            // Here, simply call addTickToSeries similar to Strategy base logic:
            series.addPrice(tick.getPrice()); // Alternatively addBar, but TA4J 0.18 might allow addPrice for dynamic series
        }

        for (int i = 1; i < ticks.size() - 1; i++) {
            Tick current = ticks.get(i);
            Tick prev = ticks.get(i - 1);
            Tick next = ticks.get(i + 1);
            double priceDiff = current.getPrice() - prev.getPrice();
            // Compute RSI for current index (if available)
            double rsiVal = 0.0;
            if (i < series.getBarCount()) {
                rsiVal = rsiIndicator.getValue(i).doubleValue();
            }
            // Determine class label based on next tick's price movement
            String direction = (next.getPrice() > current.getPrice()) ? "UP" : "DOWN";

            // Create instance
            DenseInstance inst = new DenseInstance(data.numAttributes());
            inst.setValue(attrPriceDiff, priceDiff);
            inst.setValue(attrRSI, rsiVal);
            inst.setValue(attrClass, direction);
            data.add(inst);
        }
        return data;
    }

    public void trainAndEvaluate(ArrayList<Tick> history) throws Exception {
        // Prepare dataset
        Instances data = prepareDataset(history);
        if (data.numInstances() == 0) {
            System.out.println("Not enough data to train the model.");
            return;
        }
        // Split into train and test sets (70% train, 30% test)
        int trainSize = (int) Math.round(data.numInstances() * 0.7);
        int testSize = data.numInstances() - trainSize;
        Instances trainData = new Instances(data, 0, trainSize);
        Instances testData = new Instances(data, trainSize, testSize);

        // Build a J48 decision tree classifier
        Classifier model = new J48();
        model.buildClassifier(trainData);

        // Evaluate on test set
        Evaluation eval = new Evaluation(trainData);
        eval.evaluateModel(model, testData);
        System.out.println("Model Accuracy on test data: " + String.format("%.2f%%", eval.pctCorrect()));

        // Print sample predictions vs actual for the first 10 test instances
        System.out.println("\nSample Predictions vs Actual:");
        int maxPrint = Math.min(10, testData.numInstances());
        for (int j = 0; j < maxPrint; j++) {
            double actualClassIndex = testData.instance(j).classValue();
            String actualLabel = testData.classAttribute().value((int) actualClassIndex);
            double predClassIndex = model.classifyInstance(testData.instance(j));
            String predictedLabel = testData.classAttribute().value((int) predClassIndex);
            System.out.println("Instance " + j + ": Predicted = " + predictedLabel + ", Actual = " + actualLabel);
        }
    }

    public static void main(String[] args) throws Exception {
        BacktestEngine engine = new BacktestEngine();
        ArrayList<Tick> history = new ArrayList<>(engine.loadHistoricalTicks());
        if (history.isEmpty()) {
            System.err.println("No historical data available for model training.");
            return;
        }
        WekaModelTrainer trainer = new WekaModelTrainer();
        trainer.trainAndEvaluate(history);
    }
}
