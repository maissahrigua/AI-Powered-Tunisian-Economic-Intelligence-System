package tn.isg.economics.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import tn.isg.economics.model.PricePrediction;
import tn.isg.economics.service.ReportGenerator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class LLMReportService implements ReportGenerator {
    private static final Logger logger = Logger.getLogger(LLMReportService.class.getName());
    private ChatLanguageModel chatModel;
    private boolean isLocalModel;

    public LLMReportService(boolean useLocalModel) {
        this.isLocalModel = useLocalModel;
        initializeModel();
    }

    private void initializeModel() {
        try {
            if (isLocalModel) {
                logger.info("Initializing local LLM (Ollama)...");
                chatModel = OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName("llama2")
                        .temperature(0.7)
                        .build();
                logger.info("Local LLM initialized successfully");
                logger.info("Note: Make sure Ollama is running! (ollama serve)");
            } else {
                logger.info("Initializing OpenAI LLM...");
                String apiKey = System.getenv("OPENAI_API_KEY");
                if (apiKey == null || apiKey.isEmpty()) {
                    logger.warning("OPENAI_API_KEY environment variable not set!");
                    logger.warning("Falling back to mock responses...");
                    chatModel = null;
                } else {
                    chatModel = OpenAiChatModel.builder()
                            .apiKey(apiKey)
                            .modelName("gpt-3.5-turbo")
                            .temperature(0.7)
                            .build();
                    logger.info("OpenAI LLM initialized successfully");
                }
            }
        } catch (Exception e) {
            logger.severe("Failed to initialize LLM: " + e.getMessage());
            logger.info("Will use fallback mock responses");
            chatModel = null;
        }
    }

    @Override
    public String generateMarketReport(List<PricePrediction> predictions) {
        if (predictions == null || predictions.isEmpty()) {
            throw new IllegalArgumentException("Predictions list cannot be null or empty");
        }
        logger.info("Generating comprehensive market report for " + predictions.size() + " predictions");
        String dataSummary = predictions.stream()
                .map(p -> String.format(
                        "Product: %s, Predicted Price: %.2f TND, Confidence: %.2f%%",
                        p.productType().getFrenchName(),
                        p.predictedPrice(),
                        p.confidence() * 100
                ))
                .collect(Collectors.joining("\n"));
        String prompt = String.format(
                """
                Analyze the following price predictions for Tunisian agricultural exports and generate an intelligent market report:
                
                %s
                
                Provide a detailed analysis including:
                1. General market trends
                2. Strategic recommendations for exporters
                3. Identified risks
                4. Export opportunities
                
                Write the report in a professional tone suitable for business decision-makers.
                """,
                dataSummary
        );
        if (chatModel != null) {
            try {
                String report = chatModel.generate(prompt);
                logger.info("Market report generated successfully");
                return report;
            } catch (Exception e) {
                logger.warning("LLM generation failed: " + e.getMessage());
                return generateFallbackReport(predictions);
            }
        } else {
            return generateFallbackReport(predictions);
        }
    }

    @Override
    public String generateSummaryReport(List<PricePrediction> predictions) {
        if (predictions == null || predictions.isEmpty()) {
            throw new IllegalArgumentException("Predictions list cannot be null or empty");
        }
        logger.info("Generating executive summary for " + predictions.size() + " predictions");
        String prompt = String.format(
                """
                Generate an executive summary in 3-4 sentences for the following Tunisian agricultural export price predictions:
                
                %s
                
                Focus on key insights and overall market direction.
                """,
                predictions.stream()
                        .map(p -> p.productType().getFrenchName() + ": " + p.predictedPrice() + " TND")
                        .collect(Collectors.joining(", "))
        );
        if (chatModel != null) {
            try {
                String summary = chatModel.generate(prompt);
                logger.info("Summary report generated successfully");
                return summary;
            } catch (Exception e) {
                logger.warning("LLM generation failed: " + e.getMessage());
                return generateFallbackSummary(predictions);
            }
        } else {
            return generateFallbackSummary(predictions);
        }
    }

    private String generateFallbackReport(List<PricePrediction> predictions) {
        StringBuilder report = new StringBuilder();
        report.append("=== MARKET INTELLIGENCE REPORT ===\n");
        report.append("(Generated without LLM - Basic Analysis)\n\n");
        report.append("PRICE PREDICTIONS SUMMARY:\n");
        for (PricePrediction pred : predictions) {
            report.append(String.format("- %s: %.2f TND (Confidence: %.1f%%)\n",
                    pred.productType().getFrenchName(),
                    pred.predictedPrice(),
                    pred.confidence() * 100));
        }
        double avgPrice = predictions.stream()
                .mapToDouble(PricePrediction::predictedPrice)
                .average()
                .orElse(0.0);
        double avgConfidence = predictions.stream()
                .mapToDouble(PricePrediction::confidence)
                .average()
                .orElse(0.0);
        report.append(String.format("\nAVERAGE PREDICTED PRICE: %.2f TND\n", avgPrice));
        report.append(String.format("AVERAGE CONFIDENCE: %.1f%%\n", avgConfidence * 100));
        report.append("\nNote: For detailed AI-generated insights, please configure LLM integration.\n");
        return report.toString();
    }

    private String generateFallbackSummary(List<PricePrediction> predictions) {
        double avgPrice = predictions.stream()
                .mapToDouble(PricePrediction::predictedPrice)
                .average()
                .orElse(0.0);
        return String.format(
                "Executive Summary: Analyzed %d Tunisian agricultural export predictions. " +
                        "Average predicted price: %.2f TND. " +
                        "Overall market confidence is moderate. " +
                        "Detailed insights require LLM integration.",
                predictions.size(),
                avgPrice
        );
    }

    public boolean isLLMReady() {
        return chatModel != null;
    }

    public String getModelInfo() {
        if (chatModel == null) {
            return "LLM not initialized - using fallback mode";
        }
        return isLocalModel ? "Ollama (Local Model)" : "OpenAI (Cloud API)";
    }
}