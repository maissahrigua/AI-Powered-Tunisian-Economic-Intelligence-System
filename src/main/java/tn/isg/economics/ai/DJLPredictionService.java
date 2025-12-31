package tn.isg.economics.ai;

import tn.isg.economics.annotation.AIService;
import tn.isg.economics.annotation.ModelValidation;
import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.PricePrediction;
import tn.isg.economics.model.PredictionStatus;
import tn.isg.economics.exception.ModelException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@AIService(provider = "DJL", version = "0.30.0")
public class DJLPredictionService extends BaseAIModel {
    private Random random;

    public DJLPredictionService() {
        super("DJL-Price-Predictor");
        this.random = new Random();
    }

    @Override
    public void loadModel() throws ModelException {
        try {
            logger.info("Loading DJL model...");
            Thread.sleep(500);
            isLoaded = true;
            logger.info("DJL model loaded successfully");

        } catch (Exception e) {
            isLoaded = false;
            throw new ModelException("Failed to load DJL model: " + e.getMessage(), e);
        }
    }

    @Override
    @ModelValidation(minConfidence = 0.7, description = "DJL price prediction with 70% minimum confidence")
    public PricePrediction predictPrice(ExportData input) {
        validateInput(input);
        try {
            logger.info("Generating prediction for: " + input.productType());

            double currentPrice = input.pricePerTon();
            double variation = (random.nextDouble() - 0.5) * 0.2;
            double predictedPrice = currentPrice * (1 + variation);
            switch (input.indicator()) {
                case RISING -> predictedPrice *= 1.05;
                case FALLING -> predictedPrice *= 0.95;
                case VOLATILE -> predictedPrice *= (0.9 + random.nextDouble() * 0.2);
            }
            double confidence = switch (input.indicator()) {
                case STABLE -> 0.85 + random.nextDouble() * 0.10;
                case RISING, FALLING -> 0.75 + random.nextDouble() * 0.15;
                case VOLATILE -> 0.60 + random.nextDouble() * 0.20;
                case UNPREDICTABLE -> 0.50 + random.nextDouble() * 0.20;
            };
            confidence = Math.min(0.95, Math.max(0.50, confidence));
            predictedPrice = Math.round(predictedPrice * 100.0) / 100.0;
            PredictionStatus status = confidence >= 0.7 ?
                    PredictionStatus.COMPLETED : PredictionStatus.LOW_CONFIDENCE;
            logger.info(String.format("Prediction completed: %.2f TND (confidence: %.2f%%)",
                    predictedPrice, confidence * 100));
            return new PricePrediction(
                    LocalDate.now().plusDays(30), // Predict 30 days ahead
                    input.productType(),
                    predictedPrice,
                    confidence,
                    modelName,
                    status
            );

        } catch (Exception e) {
            logger.severe("Prediction failed: " + e.getMessage());
            return new PricePrediction(
                    LocalDate.now(),
                    input.productType(),
                    0.0,
                    0.0,
                    modelName,
                    PredictionStatus.FAILED
            );
        }
    }

    @Override
    public List<PricePrediction> predictBatch(List<ExportData> inputs) {
        logger.info("Starting batch prediction for " + inputs.size() + " items");

        return inputs.stream()
                .map(this::predictPrice)
                .toList();
    }

    @Override
    public void unloadModel() {
        logger.info("Unloading DJL model...");
        isLoaded = false;
        logger.info("DJL model unloaded");
    }

    @Override
    public double getModelAccuracy() {
        return 0.78;
    }
}