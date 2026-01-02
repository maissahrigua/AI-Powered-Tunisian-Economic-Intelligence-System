package tn.isg.economics;

import tn.isg.economics.ai.DJLPredictionService;
import tn.isg.economics.ai.ONNXRuntimeService;
import tn.isg.economics.ai.LLMReportService;
import tn.isg.economics.model.*;
import tn.isg.economics.service.EconomicIntelligenceService;
import tn.isg.economics.exception.ModelException;
import tn.isg.economics.exception.PredictionException;
import tn.isg.economics.util.DataGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   Tunisian Agricultural Export AI Intelligence System    ║");
        System.out.println("║              Powered by Java 25 + AI/ML                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        try {
            System.out.println(">>> STEP 1: Initializing AI Services...");
            System.out.println();
            System.out.println("Initializing DJL Prediction Service...");
            var djlPredictionService = new DJLPredictionService();
            djlPredictionService.loadModel();
            System.out.println("✓ DJL Service ready!");
            System.out.println();
            System.out.println("Initializing LLM Report Service...");
            var llmReportService = new LLMReportService(true);
            System.out.println("✓ LLM Service initialized!");
            System.out.println("  Model: " + llmReportService.getModelInfo());
            System.out.println("  Status: " + (llmReportService.isLLMReady() ? "Ready" : "Fallback Mode"));
            System.out.println();
            System.out.println(">>> STEP 2: Creating Economic Intelligence Service...");
            var intelligenceService = new EconomicIntelligenceService(
                    djlPredictionService,
                    llmReportService
            );
            System.out.println("✓ Service created!");
            System.out.println();
            System.out.println(intelligenceService.getServiceInfo());
            System.out.println();
            System.out.println(">>> STEP 3: Creating Sample Tunisian Export Data...");
            System.out.println();
            List<ExportData> sampleExports = DataGenerator.generateExports(100);
            System.out.println("✓ Created " + sampleExports.size() + " sample export records");
            System.out.println();
            System.out.println("Sample Export Data:");
            System.out.println("─".repeat(80));
            for (ExportData export : sampleExports) {
                System.out.printf("%-15s | %8.2f TND/ton | %6.1f tons | %-10s | %s%n",
                        export.productType().getFrenchName(),
                        export.pricePerTon(),
                        export.volume(),
                        export.destinationCountry(),
                        export.indicator()
                );
            }
            System.out.println("─".repeat(80));
            System.out.println();
            System.out.println(">>> STEP 4: Generating AI Price Predictions...");
            System.out.println();
            List<PricePrediction> predictions = intelligenceService.analyzeExports(sampleExports);
            System.out.println("✓ Generated " + predictions.size() + " predictions");
            System.out.println();
            System.out.println("AI Price Predictions (30 days ahead):");
            System.out.println("─".repeat(90));
            System.out.printf("%-15s | %-12s | %-12s | %-10s | %-8s%n",
                    "Product", "Current Price", "Predicted", "Confidence", "Status");
            System.out.println("─".repeat(90));
            for (int i = 0; i < sampleExports.size() && i < predictions.size(); i++) {
                ExportData export = sampleExports.get(i);
                PricePrediction pred = predictions.get(i);
                System.out.printf("%-15s | %8.2f TND | %8.2f TND | %8.1f%% | %-8s%n",
                        pred.productType().getFrenchName(),
                        export.pricePerTon(),
                        pred.predictedPrice(),
                        pred.confidence() * 100,
                        pred.status()
                );
            }
            System.out.println("─".repeat(90));
            System.out.println();
            System.out.println(">>> STEP 5: Prediction Statistics...");
            System.out.println();
            System.out.println(intelligenceService.getPredictionStatistics(predictions));
            System.out.println(">>> STEP 6: Grouping Predictions by Product...");
            System.out.println();
            Map<ProductType, List<PricePrediction>> groupedPredictions =
                    intelligenceService.groupByProduct(predictions);
            System.out.println("Predictions by Product Type:");
            groupedPredictions.forEach((product, preds) -> {
                System.out.printf("  %s: %d prediction(s)%n",
                        product.getFrenchName(),
                        preds.size()
                );
            });
            System.out.println();
            System.out.println(">>> STEP 7: Average Predicted Prices by Product...");
            System.out.println();
            Map<ProductType, Double> avgPrices =
                    intelligenceService.calculateAveragePriceByProduct(predictions);
            System.out.println("Average Predicted Prices:");
            avgPrices.forEach((product, avgPrice) -> {
                System.out.printf("  %s: %.2f TND/ton%n",
                        product.getFrenchName(),
                        avgPrice
                );
            });
            System.out.println();
            System.out.println(">>> STEP 8: Finding Highest Confidence Prediction...");
            System.out.println();
            PricePrediction bestPrediction = intelligenceService.findBestPrediction(predictions);
            if (bestPrediction != null) {
                System.out.printf("✓ Best Prediction: %s - %.2f TND (%.1f%% confidence)%n",
                        bestPrediction.productType().getFrenchName(),
                        bestPrediction.predictedPrice(),
                        bestPrediction.confidence() * 100
                );
            }
            System.out.println();
            System.out.println(">>> STEP 9: Generating AI Market Intelligence Report...");
            System.out.println();
            String marketReport = intelligenceService.generateIntelligenceReport(predictions);
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              AI-GENERATED MARKET REPORT                    ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println(marketReport);
            System.out.println();
            System.out.println(">>> STEP 10: Generating Executive Summary...");
            System.out.println();
            String summary = intelligenceService.generateSummary(predictions);
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                  EXECUTIVE SUMMARY                         ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println(summary);
            System.out.println();
            System.out.println(">>> Cleaning up resources...");
            djlPredictionService.unloadModel();
            System.out.println("✓ Models unloaded");
            System.out.println();
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║            SYSTEM DEMONSTRATION COMPLETED! ✓               ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
        } catch (ModelException e) {
            System.err.println("❌ Model Error: " + e.getMessage());
            e.printStackTrace();
        } catch (PredictionException e) {
            System.err.println("❌ Prediction Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}