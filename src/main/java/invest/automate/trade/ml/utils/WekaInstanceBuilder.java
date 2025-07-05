package invest.automate.trade.ml.utils;

import com.zerodhatech.models.Tick;
import org.ta4j.core.Bar;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WekaInstanceBuilder {

    /**
     * Build the feature vector (Instance) for ML, using bar, tick, and indicator values.
     *
     * @param tick        Zerodha Tick object (may be null if only using bar/indicator)
     * @param bar         Current TA4J Bar
     * @param indicators  Map of indicator name -> value (Double), e.g. "RSI" -> 51.2
     * @param classLabels Optional: list of class labels ("UP","DOWN","NONE") for classification (null for prediction)
     * @return Pair<Instances, Instance>: the Weka Instances definition, and the built Instance (ready for ML model)
     */
    public static Pair<Instances, Instance> buildInstance(
            Tick tick,
            Bar bar,
            Map<String, Double> indicators,
            List<String> classLabels // Pass null for prediction
    ) {
        List<Attribute> attributes = new ArrayList<>();

        // --- Basic bar features
        attributes.add(new Attribute("open"));
        attributes.add(new Attribute("high"));
        attributes.add(new Attribute("low"));
        attributes.add(new Attribute("close"));
        attributes.add(new Attribute("volume"));

        // --- Tick features (optional, if present)
        if (tick != null) {
            attributes.add(new Attribute("ltp")); // last traded price
            attributes.add(new Attribute("ltq")); // last traded quantity
        }

        // --- Indicator features
        if (indicators != null) {
            for (String name : indicators.keySet()) {
                attributes.add(new Attribute(name));
            }
        }

        // --- Class attribute (if provided)
        if (classLabels != null && !classLabels.isEmpty()) {
            attributes.add(new Attribute("class", classLabels));
        }

        // Build Instances definition
        Instances dataset = new Instances("TradeFeatures", (ArrayList<Attribute>) attributes, 0);
        if (classLabels != null && !classLabels.isEmpty()) {
            dataset.setClassIndex(attributes.size() - 1);
        }

        // Build feature vector
        double[] vals = new double[attributes.size()];
        int idx = 0;
        vals[idx++] = bar.getOpenPrice().doubleValue();
        vals[idx++] = bar.getHighPrice().doubleValue();
        vals[idx++] = bar.getLowPrice().doubleValue();
        vals[idx++] = bar.getClosePrice().doubleValue();
        vals[idx++] = bar.getVolume().doubleValue();

        if (tick != null) {
            vals[idx++] = tick.getLastTradedPrice();
            vals[idx++] = tick.getLastTradedQuantity();
        }

        if (indicators != null) {
            for (String name : indicators.keySet()) {
                vals[idx++] = indicators.get(name);
            }
        }

        // For prediction, do not set class; for training, set it after this call
        Instance instance = new DenseInstance(1.0, vals);
        instance.setDataset(dataset);

        return new Pair<>(dataset, instance);
    }

    // Simple Pair utility for return value
    public static class Pair<K, V> {
        public final K first;
        public final V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }
    }
}
