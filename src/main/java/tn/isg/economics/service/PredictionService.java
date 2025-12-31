package tn.isg.economics.service;

import tn.isg.economics.model.ExportData;
import tn.isg.economics.model.PricePrediction;
import java.util.List;

public interface PredictionService {
    PricePrediction predictPrice(ExportData input);
    List<PricePrediction> predictBatch(List<ExportData> inputs);
    double getModelAccuracy();
}