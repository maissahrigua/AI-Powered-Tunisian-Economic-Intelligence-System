package tn.isg.economics.util;

import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.MarketIndicator;
import tn.isg.economics.model.ProductType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVDataLoader {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static List<ExportData> loadFromCSV(String filename) throws IOException {
        if (!Files.exists(Paths.get(filename))) {
            throw new IllegalArgumentException("File not found: " + filename);
        }
        System.out.println("Loading data from CSV: " + filename);
        List<ExportData> exports = new ArrayList<>();
        int lineNumber = 0;
        int skippedLines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            line = reader.readLine();
            lineNumber++;
            if (line == null) {
                throw new IllegalArgumentException("CSV file is empty");
            }
            if (!line.toLowerCase().contains("date") ||
                    !line.toLowerCase().contains("product")) {
                System.out.println("Warning: CSV header might be invalid. Expected: " +
                        "date,product,pricePerTon,volume,destinationCountry,marketIndicator");
            }
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    ExportData export = parseCSVLine(line);
                    exports.add(export);
                } catch (Exception e) {
                    System.err.println("Warning: Skipped line " + lineNumber +
                            " due to error: " + e.getMessage());
                    skippedLines++;
                }
            }
        }
        System.out.println("✓ Loaded " + exports.size() + " records from CSV");
        if (skippedLines > 0) {
            System.out.println("  (Skipped " + skippedLines + " invalid lines)");
        }
        return exports;
    }

    private static ExportData parseCSVLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 6) {
            throw new IllegalArgumentException(
                    "Invalid CSV line format. Expected 6 columns, found " + parts.length
            );
        }
        try {
            LocalDate date = LocalDate.parse(parts[0].trim(), DATE_FORMATTER);
            ProductType product = ProductType.valueOf(parts[1].trim().toUpperCase());
            double pricePerTon = Double.parseDouble(parts[2].trim());
            double volume = Double.parseDouble(parts[3].trim());
            String country = parts[4].trim();
            MarketIndicator indicator = MarketIndicator.valueOf(parts[5].trim().toUpperCase());
            return new ExportData(date, product, pricePerTon, volume, country, indicator);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Error parsing CSV line: " + line + " - " + e.getMessage()
            );
        }
    }

    public static List<ExportData> loadAndValidate(String filename) throws IOException {
        List<ExportData> allData = loadFromCSV(filename);
        List<ExportData> validData = allData.stream()
                .filter(export -> export.pricePerTon() > 0)
                .filter(export -> export.volume() > 0)
                .toList();
        int invalidCount = allData.size() - validData.size();
        if (invalidCount > 0) {
            System.out.println("Filtered out " + invalidCount + " invalid records");
        }
        return validData;
    }

    public static int countRecords(String filename) throws IOException {
        if (!Files.exists(Paths.get(filename))) {
            throw new IllegalArgumentException("File not found: " + filename);
        }
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.readLine();
            while (reader.readLine() != null) {
                count++;
            }
        }
        return count;
    }

    public static List<ExportData> previewCSV(String filename, int limit) throws IOException {
        if (!Files.exists(Paths.get(filename))) {
            throw new IllegalArgumentException("File not found: " + filename);
        }
        System.out.println("Previewing first " + limit + " records from: " + filename);
        List<ExportData> preview = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.readLine();
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < limit) {
                try {
                    ExportData export = parseCSVLine(line);
                    preview.add(export);
                    count++;
                } catch (Exception e) {
                    System.err.println("Warning: Skipped invalid line");
                }
            }
        }
        System.out.println("✓ Loaded " + preview.size() + " preview records");
        return preview;
    }
}