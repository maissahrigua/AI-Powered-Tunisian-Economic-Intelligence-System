package tn.isg.economics.util;

import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.ProductType;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsCalculator {

    public static double calculateAverage(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list cannot be null or empty");
        }
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public static double calculateSum(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public static double calculateMin(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list cannot be null or empty");
        }
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    public static double calculateMax(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list cannot be null or empty");
        }
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    public static double calculateMedian(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list cannot be null or empty");
        }
        // Sort values to find middle
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0) {
            // Even number: average of two middle values
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            // Odd number: middle value
            return sorted.get(size / 2);
        }
    }

    public static double calculateVariance(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        double average = calculateAverage(values);
        double sumSquaredDifferences = values.stream()
                .mapToDouble(value -> Math.pow(value - average, 2))
                .sum();
        return sumSquaredDifferences / values.size();
    }

    public static double calculateStandardDeviation(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        return Math.sqrt(calculateVariance(values));
    }

    public static double calculatePercentile(List<Double> values, double percentile) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values list cannot be null or empty");
        }
        if (percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("Percentile must be between 0 and 100");
        }
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));
        return sorted.get(index);
    }

    public static PriceStatistics getPriceStatistics(List<ExportData> exports) {
        if (exports == null || exports.isEmpty()) {
            return new PriceStatistics(0, 0, 0, 0, 0, 0);
        }
        List<Double> prices = exports.stream()
                .map(ExportData::pricePerTon)
                .toList();
        return new PriceStatistics(
                calculateAverage(prices),
                calculateMin(prices),
                calculateMax(prices),
                calculateMedian(prices),
                calculateStandardDeviation(prices),
                prices.size()
        );
    }

    public static Map<ProductType, Double> getAveragePriceByProduct(List<ExportData> exports) {
        if (exports == null || exports.isEmpty()) {
            return new HashMap<>();
        }
        return exports.stream()
                .collect(Collectors.groupingBy(
                        ExportData::productType,
                        Collectors.averagingDouble(ExportData::pricePerTon)
                ));
    }

    public static Map<String, Double> getTotalVolumeByCountry(List<ExportData> exports) {
        if (exports == null || exports.isEmpty()) {
            return new HashMap<>();
        }
        return exports.stream()
                .collect(Collectors.groupingBy(
                        ExportData::destinationCountry,
                        Collectors.summingDouble(ExportData::volume)
                ));
    }

    public static Map<ProductType, Double> getTotalVolumeByProduct(List<ExportData> exports) {
        if (exports == null || exports.isEmpty()) {
            return new HashMap<>();
        }
        return exports.stream()
                .collect(Collectors.groupingBy(
                        ExportData::productType,
                        Collectors.summingDouble(ExportData::volume)
                ));
    }

    public static Map<ProductType, Double> getTotalRevenueByProduct(List<ExportData> exports) {
        if (exports == null || exports.isEmpty()) {
            return new HashMap<>();
        }
        return exports.stream()
                .collect(Collectors.groupingBy(
                        ExportData::productType,
                        Collectors.summingDouble(e -> e.pricePerTon() * e.volume())
                ));
    }

    public static Map<ProductType, Double> getAverageVolumeByProduct(List<ExportData> exports) {
        if (exports == null || exports.isEmpty()) {
            return new HashMap<>();
        }
        return exports.stream()
                .collect(Collectors.groupingBy(
                        ExportData::productType,
                        Collectors.averagingDouble(ExportData::volume)
                ));
    }

    public static Map<ProductType, Long> getExportCountByProduct(List<ExportData> exports) {
        if (exports == null || exports.isEmpty()) {
            return new HashMap<>();
        }
        return exports.stream()
                .collect(Collectors.groupingBy(
                        ExportData::productType,
                        Collectors.counting()
                ));
    }

    public static Optional<ProductType> getMostExpensiveProduct(List<ExportData> exports) {
        Map<ProductType, Double> avgPrices = getAveragePriceByProduct(exports);
        return avgPrices.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public static Optional<ProductType> getCheapestProduct(List<ExportData> exports) {
        Map<ProductType, Double> avgPrices = getAveragePriceByProduct(exports);
        return avgPrices.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public static Map<ProductType, Double> getPriceRangeByProduct(List<ExportData> exports) {
        if (exports == null || exports.isEmpty()) {
            return new HashMap<>();
        }
        Map<ProductType, Double> result = new HashMap<>();
        Map<ProductType, List<ExportData>> grouped = exports.stream()
                .collect(Collectors.groupingBy(ExportData::productType));
        grouped.forEach((product, list) -> {
            List<Double> prices = list.stream()
                    .map(ExportData::pricePerTon)
                    .toList();
            double range = calculateMax(prices) - calculateMin(prices);
            result.put(product, range);
        });
        return result;
    }
}