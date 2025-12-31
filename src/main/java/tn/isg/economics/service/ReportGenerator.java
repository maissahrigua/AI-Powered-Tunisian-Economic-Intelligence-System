package tn.isg.economics.service;

import tn.isg.economics.model.PricePrediction;
import java.util.List;

public interface ReportGenerator {
    String generateMarketReport(List<PricePrediction> predictions);
    String generateSummaryReport(List<PricePrediction> predictions);
}