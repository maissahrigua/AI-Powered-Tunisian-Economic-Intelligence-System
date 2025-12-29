package tn.isg.economics.model;

import java . time . LocalDate ;

public record PricePrediction(
        LocalDate predictionDate,
        ProductType productType,
        double predictedPrice,
        double confidence,
        String modelName,
        PredictionStatus status
) {
    public PricePrediction {
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }
    }

    public double getConfidencePercentage() {
        return confidence * 100;
    }

    public boolean isReliable(double threshold) {
        return confidence >= threshold;
    }

    public boolean isReliable() {
        return isReliable(0.7);
    }
}