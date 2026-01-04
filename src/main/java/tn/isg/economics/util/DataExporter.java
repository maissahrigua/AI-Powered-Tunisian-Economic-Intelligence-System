package tn.isg.economics.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.PricePrediction;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataExporter {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void exportToCSV(List<ExportData> exports, String filename) throws IOException {
        if (exports == null || exports.isEmpty()) {
            throw new IllegalArgumentException("Exports list cannot be null or empty");
        }
        System.out.println("Exporting " + exports.size() + " records to CSV: " + filename);
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
            System.out.println("Created directory: " + parentDir.getPath());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("date,product,pricePerTon,volume,destinationCountry,marketIndicator");
            writer.newLine();
            for (ExportData export : exports) {
                String line = String.format(java.util.Locale.US, "%s,%s,%.2f,%.2f,%s,%s",
                        export.date(),
                        export.productType().name(),
                        export.pricePerTon(),
                        export.volume(),
                        export.destinationCountry(),
                        export.indicator().name()
                );
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println("✓ Successfully exported to: " + file.getAbsolutePath());
    }

    public static void exportPredictionsToCSV(List<PricePrediction> predictions, String filename)
            throws IOException {
        if (predictions == null || predictions.isEmpty()) {
            throw new IllegalArgumentException("Predictions list cannot be null or empty");
        }
        System.out.println("Exporting " + predictions.size() + " predictions to CSV: " + filename);
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("predictionDate,product,predictedPrice,confidence,modelName,status");
            writer.newLine();
            for (PricePrediction prediction : predictions) {
                String line = String.format(java.util.Locale.US, "%s,%s,%.2f,%.4f,%s,%s",
                        prediction.predictionDate(),
                        prediction.productType().name(),
                        prediction.predictedPrice(),
                        prediction.confidence(),
                        prediction.modelName(),
                        prediction.status().name()
                );
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println("✓ Successfully exported predictions to: " + file.getAbsolutePath());
    }

    public static void exportToJSON(List<ExportData> exports, String filename) throws IOException {
        if (exports == null || exports.isEmpty()) {
            throw new IllegalArgumentException("Exports list cannot be null or empty");
        }
        System.out.println("Exporting " + exports.size() + " records to JSON: " + filename);
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        objectMapper.writeValue(file, exports);
        System.out.println("✓ Successfully exported to: " + file.getAbsolutePath());
    }

    public static void exportPredictionsToJSON(List<PricePrediction> predictions, String filename)
            throws IOException {
        if (predictions == null || predictions.isEmpty()) {
            throw new IllegalArgumentException("Predictions list cannot be null or empty");
        }
        System.out.println("Exporting " + predictions.size() + " predictions to JSON: " + filename);
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        objectMapper.writeValue(file, predictions);
        System.out.println("✓ Successfully exported predictions to: " + file.getAbsolutePath());
    }

    public static void exportStatisticsToText(PriceStatistics statistics, String filename)
            throws IOException {
        if (statistics == null) {
            throw new IllegalArgumentException("Statistics cannot be null");
        }
        System.out.println("Exporting statistics to text file: " + filename);
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(statistics.toFormattedString());
        }
        System.out.println("✓ Successfully exported statistics to: " + file.getAbsolutePath());
    }
}