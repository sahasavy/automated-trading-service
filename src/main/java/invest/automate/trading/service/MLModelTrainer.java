package invest.automate.trading.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.BufferedReader;
import java.io.FileReader;

@Slf4j
@Service
public class MLModelTrainer {

    private static final String MODEL_FILE = "trained_model.model";

    public Classifier trainModel(String dataPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataPath))) {
            Instances data = new Instances(reader);
            data.setClassIndex(data.numAttributes() - 1);

            RandomForest model = new RandomForest();
            model.buildClassifier(data);

            SerializationHelper.write(MODEL_FILE, model);
            log.info("Model trained and saved successfully.");

            return model;
        } catch (Exception e) {
            log.error("Error training model: {}", e.getMessage(), e);
            return null;
        }
    }

    public Classifier loadModel() {
        try {
            return (Classifier) SerializationHelper.read(MODEL_FILE);
        } catch (Exception e) {
            log.error("Error loading model: {}", e.getMessage(), e);
            return null;
        }
    }
}
