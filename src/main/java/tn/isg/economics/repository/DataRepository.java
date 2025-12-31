package tn.isg.economics.repository;

import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.PricePrediction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DataRepository {
    boolean saveExportData(ExportData exportData);
    List<ExportData> getAllExportData();
    List<ExportData> getExportDataByDateRange(LocalDate startDate, LocalDate endDate);
    boolean savePrediction(PricePrediction prediction);
    List<PricePrediction> getAllPredictions();
    void clearAll();
}