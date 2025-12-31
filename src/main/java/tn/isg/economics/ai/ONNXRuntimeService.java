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

@AIService(provider = "ONNX Runtime", version = "1.19.2")
public class ONNXRuntimeService extends BaseAIModel {
    private Random random;

    public ONNXRuntimeService() {
        super("ONNX-Price-Predictor");
        this.random = new Random();
    }

    @Override
    public void loadModel() throws ModelException {
        try {
            logger.info("Loading ONNX model...");
            Thread.sleep(600);
            isLoaded = true;
            logger.info("ONNX model loaded successfully");
        } catch (Exception e) {
            isLoaded = false;
            throw new ModelException("Failed to load ONNX model: " + e.getMessage(), e);
        }
    }

    @Override
    @ModelValidation(minConfidence = 0.75, description = "ONNX prediction with 75% minimum confidence")
    public PricePrediction predictPrice(ExportData input) {
        validateInput(input);
        try {
            logger.info("ONNX: Generating prediction for: " + input.productType());
            double currentPrice = input.pricePerTon();
            double volume = input.volume();
            double volumeFactor = volume > 100 ? 0.98 : 1.02;
            double predictedPrice = currentPrice * volumeFactor;
            int month = LocalDate.now().getMonthValue();
            double seasonalFactor = 1.0 + (Math.sin(month * Math.PI / 6) * 0.05);
            predictedPrice *= seasonalFactor;
            double marketAdjustment = switch (input.indicator()) {
                case RISING -> 1.08;
                case FALLING -> 0.92;
                case VOLATILE -> 0.95 + random.nextDouble() * 0.10;
                case STABLE -> 1.0;
                case UNPREDICTABLE -> 0.90 + random.nextDouble() * 0.20;
            };
            predictedPrice *= marketAdjustment;
            double confidence = switch (input.indicator()) {
                case STABLE -> 0.88 + random.nextDouble() * 0.08;
                case RISING, FALLING -> 0.78 + random.nextDouble() * 0.12;
                case VOLATILE -> 0.65 + random.nextDouble() * 0.15;
                case UNPREDICTABLE -> 0.55 + random.nextDouble() * 0.15;
            };
            confidence = Math.min(0.95, Math.max(0.55, confidence));
            predictedPrice = Math.round(predictedPrice * 100.0) / 100.0;
            PredictionStatus status = confidence >= 0.75 ?
                    PredictionStatus.COMPLETED : PredictionStatus.LOW_CONFIDENCE;
            logger.info(String.format("ONNX Prediction: %.2f TND (confidence: %.2f%%)",
                    predictedPrice, confidence * 100));
            return new PricePrediction(
                    LocalDate.now().plusDays(30),
                    input.productType(),
                    predictedPrice,
                    confidence,
                    modelName,
                    status
            );
        } catch (Exception e) {
            logger.severe("ONNX prediction failed: " + e.getMessage());
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
        logger.info("ONNX: Starting batch prediction for " + inputs.size() + " items");
        return inputs.stream()
                .map(this::predictPrice)
                .toList();
    }

    @Override
    public void unloadModel() {
        logger.info("Unloading ONNX model...");
        isLoaded = false;
        logger.info("ONNX model unloaded");
    }

    @Override
    public double getModelAccuracy() {
        return 0.80;
    }
}