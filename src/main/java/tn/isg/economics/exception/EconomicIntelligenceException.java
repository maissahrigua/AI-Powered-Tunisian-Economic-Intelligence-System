package tn.isg.economics.exception;

public class EconomicIntelligenceException extends Exception {
    public EconomicIntelligenceException(String message) {
        super(message);
    }

    public EconomicIntelligenceException(String message, Throwable cause) {
        super(message, cause);
    }
}