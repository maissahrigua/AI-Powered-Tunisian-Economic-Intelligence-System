package tn.isg.economics.service;

import tn.isg.economics.ai.BaseAIModel;
import tn.isg.economics.ai.LLMReportService;
import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.PricePrediction;
import tn.isg.economics.model.PredictionStatus;
import tn.isg.economics.model.ProductType;
import tn.isg.economics.exception.PredictionException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class EconomicIntelligenceService {
    private static final Logger log = Logger.getLogger(EconomicIntelligenceService.class.getName());
    private final BaseAIModel predictionModel;
    private final LLMReportService reportService;

    public EconomicIntelligenceService(BaseAIModel predictionModel, LLMReportService reportService) {
        this.predictionModel = predictionModel;
        this.reportService = reportService;
    }

    public BaseAIModel getPredictionModel() {
        return predictionModel;
    }

    public LLMReportService getReportService() {
        return reportService;
    }

    public List<PricePrediction> analyzeExports(List<ExportData> exports) throws PredictionException {
        if (exports == null || exports.isEmpty()) {
            throw new PredictionException("Export data list cannot be null or empty");
        }
        log.info("Analyzing " + exports.size() + " export records");
        try {
            Predicate<ExportData> isValidExport = export ->
                    export.pricePerTon() > 0 && export.volume() > 0;
            List<ExportData> validExports = exports.stream()
                    .filter(isValidExport)
                    .collect(Collectors.toList());
            log.info("Valid exports after filtering: " + validExports.size());
            if (validExports.isEmpty()) {
                throw new PredictionException("No valid export data found after filtering");
            }
            List<PricePrediction> predictions = predictionModel.predictBatch(validExports);
            log.info("Generated " + predictions.size() + " predictions");
            List<PricePrediction> highConfidencePredictions = predictions.stream()
                    .filter(p -> p.confidence() > 0.7)
                    .collect(Collectors.toList());
            log.info("High-confidence predictions: " + highConfidencePredictions.size());
            return highConfidencePredictions;
        } catch (Exception e) {
            log.severe("Error during export analysis: " + e.getMessage());
            throw new PredictionException("Failed to analyze exports", e);
        }
    }

    public String generateIntelligenceReport(List<PricePrediction> predictions) {
        if (predictions == null || predictions.isEmpty()) {
            log.warning("No predictions provided for report generation");
            return "No predictions available to generate report.";
        }
        log.info("Generating intelligence report for " + predictions.size() + " predictions");
        return reportService.generateMarketReport(predictions);
    }

    public String generateSummary(List<PricePrediction> predictions) {
        if (predictions == null || predictions.isEmpty()) {
            return "No predictions available for summary.";
        }
        log.info("Generating executive summary");
        return reportService.generateSummaryReport(predictions);
    }

    public List<PricePrediction> filterByProduct(List<PricePrediction> predictions, ProductType productType) {
        log.info("Filtering predictions by product: " + productType);
        return predictions.stream()
                .filter(p -> p.productType() == productType)
                .collect(Collectors.toList());
    }

    public List<PricePrediction> filterByConfidence(List<PricePrediction> predictions, double minConfidence) {
        log.info("Filtering predictions with confidence >= " + minConfidence);
        return predictions.stream()
                .filter(p -> p.confidence() >= minConfidence)
                .collect(Collectors.toList());
    }

    public Map<ProductType, List<PricePrediction>> groupByProduct(List<PricePrediction> predictions) {
        log.info("Grouping predictions by product type");
        return predictions.stream()
                .collect(Collectors.groupingBy(PricePrediction::productType));
    }

    public Map<ProductType, Double> calculateAveragePriceByProduct(List<PricePrediction> predictions) {
        log.info("Calculating average prices by product");
        return predictions.stream()
                .collect(Collectors.groupingBy(
                        PricePrediction::productType,
                        Collectors.averagingDouble(PricePrediction::predictedPrice)
                ));
    }

    public String getPredictionStatistics(List<PricePrediction> predictions) {
        if (predictions == null || predictions.isEmpty()) {
            return "No predictions available.";
        }
        long totalPredictions = predictions.size();
        long successfulPredictions = predictions.stream()
                .filter(p -> p.status() == PredictionStatus.COMPLETED)
                .count();
        double avgConfidence = predictions.stream()
                .mapToDouble(PricePrediction::confidence)
                .average()
                .orElse(0.0);
        double avgPrice = predictions.stream()
                .mapToDouble(PricePrediction::predictedPrice)
                .average()
                .orElse(0.0);
        return String.format(
                """
                === PREDICTION STATISTICS ===
                Total Predictions: %d
                Successful: %d
                Average Confidence: %.2f%%
                Average Predicted Price: %.2f TND
                Model: %s
                Model Accuracy: %.2f%%
                """,
                totalPredictions,
                successfulPredictions,
                avgConfidence * 100,
                avgPrice,
                predictionModel.getModelName(),
                predictionModel.getModelAccuracy() * 100
        );
    }

    public PricePrediction findBestPrediction(List<PricePrediction> predictions) {
        return predictions.stream()
                .max((p1, p2) -> Double.compare(p1.confidence(), p2.confidence()))
                .orElse(null);
    }

    public boolean isModelReady() {
        return predictionModel.isModelLoaded();
    }

    public String getServiceInfo() {
        return String.format(
                """
                === SERVICE INFORMATION ===
                Prediction Model: %s
                Model Status: %s
                Model Accuracy: %.2f%%
                Report Generator: %s
                LLM Status: %s
                """,
                predictionModel.getModelName(),
                predictionModel.isModelLoaded() ? "Ready" : "Not Loaded",
                predictionModel.getModelAccuracy() * 100,
                reportService.getModelInfo(),
                reportService.isLLMReady() ? "Ready" : "Using Fallback"
        );
    }
}