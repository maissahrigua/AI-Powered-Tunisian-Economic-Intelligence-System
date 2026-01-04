package tn.isg.economics.repository;

import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.MarketIndicator;
import tn.isg.economics.model.PricePrediction;
import tn.isg.economics.model.ProductType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryDataRepository implements DataRepository {
    private final List<ExportData> exports;
    private final List<PricePrediction> predictions;

    public InMemoryDataRepository() {
        this.exports = new ArrayList<>();
        this.predictions = new ArrayList<>();
    }

    @Override
    public boolean saveExportData(ExportData exportData) {
        if (exportData == null) {
            return false;
        }
        return exports.add(exportData);
    }

    public int saveAllExportData(List<ExportData> exportDataList) {
        if (exportDataList == null || exportDataList.isEmpty()) {
            return 0;
        }
        int saved = 0;
        for (ExportData data : exportDataList) {
            if (saveExportData(data)) {
                saved++;
            }
        }
        return saved;
    }

    @Override
    public List<ExportData> getAllExportData() {
        return new ArrayList<>(exports); // Return copy to prevent external modification
    }

    @Override
    public List<ExportData> getExportDataByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        return exports.stream()
                .filter(export -> !export.date().isBefore(startDate) && !export.date().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<ExportData> findByProduct(ProductType productType) {
        if (productType == null) {
            return new ArrayList<>();
        }
        return exports.stream()
                .filter(export -> export.productType() == productType)
                .collect(Collectors.toList());
    }

    public List<ExportData> findByCountry(String country) {
        if (country == null || country.isEmpty()) {
            return new ArrayList<>();
        }
        return exports.stream()
                .filter(export -> export.destinationCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }

    public List<ExportData> findByMarketIndicator(MarketIndicator indicator) {
        if (indicator == null) {
            return new ArrayList<>();
        }
        return exports.stream()
                .filter(export -> export.indicator() == indicator)
                .collect(Collectors.toList());
    }

    public List<ExportData> findByPriceRange(double minPrice, double maxPrice) {
        return exports.stream()
                .filter(export -> export.pricePerTon() >= minPrice && export.pricePerTon() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<ExportData> findByVolumeRange(double minVolume, double maxVolume) {
        return exports.stream()
                .filter(export -> export.volume() >= minVolume && export.volume() <= maxVolume)
                .collect(Collectors.toList());
    }

    public List<ExportData> findByDate(LocalDate date) {
        if (date == null) {
            return new ArrayList<>();
        }
        return exports.stream()
                .filter(export -> export.date().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public boolean savePrediction(PricePrediction prediction) {
        if (prediction == null) {
            return false;
        }
        return predictions.add(prediction);
    }

    public int saveAllPredictions(List<PricePrediction> predictionList) {
        if (predictionList == null || predictionList.isEmpty()) {
            return 0;
        }
        int saved = 0;
        for (PricePrediction prediction : predictionList) {
            if (savePrediction(prediction)) {
                saved++;
            }
        }
        return saved;
    }

    @Override
    public List<PricePrediction> getAllPredictions() {
        return new ArrayList<>(predictions); // Return copy
    }

    public List<PricePrediction> findPredictionsByProduct(ProductType productType) {
        if (productType == null) {
            return new ArrayList<>();
        }
        return predictions.stream()
                .filter(pred -> pred.productType() == productType)
                .collect(Collectors.toList());
    }

    public List<PricePrediction> findPredictionsByConfidence(double minConfidence) {
        return predictions.stream()
                .filter(pred -> pred.confidence() >= minConfidence)
                .collect(Collectors.toList());
    }

    public List<PricePrediction> findPredictionsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        return predictions.stream()
                .filter(pred -> !pred.predictionDate().isBefore(startDate) &&
                        !pred.predictionDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public void clearAll() {
        exports.clear();
        predictions.clear();
    }

    public void clearExports() {
        exports.clear();
    }

    public void clearPredictions() {
        predictions.clear();
    }

    public int getExportCount() {
        return exports.size();
    }

    public int getPredictionCount() {
        return predictions.size();
    }

    public boolean isEmpty() {
        return exports.isEmpty() && predictions.isEmpty();
    }

    public String getRepositoryStats() {
        return String.format("""
            === REPOSITORY STATISTICS ===
            Total Exports: %d
            Total Predictions: %d
            Unique Products: %d
            Unique Countries: %d
            ============================
            """,
                exports.size(),
                predictions.size(),
                exports.stream().map(ExportData::productType).distinct().count(),
                exports.stream().map(ExportData::destinationCountry).distinct().count()
        );
    }
}