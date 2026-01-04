package tn.isg.economics.util;

public record PriceStatistics(
        double average,
        double min,
        double max,
        double median,
        double standardDeviation,
        int count
) {

    public String toFormattedString() {
        return String.format("""
            === PRICE STATISTICS ===
            Count: %d
            Average: %.2f TND
            Minimum: %.2f TND
            Maximum: %.2f TND
            Median: %.2f TND
            Standard Deviation: %.2f TND
            Range: %.2f TND
            """,
                count,
                average,
                min,
                max,
                median,
                standardDeviation,
                max - min
        );
    }

    public double getCoefficientOfVariation() {
        if (average == 0) return 0;
        return (standardDeviation / average) * 100;
    }
}