package tn.isg.economics.exception;

public class PredictionException extends EconomicIntelligenceException {
    public PredictionException(String message) {
        super(message);
    }

    public PredictionException(String message, Throwable cause) {
        super(message, cause);
    }
}