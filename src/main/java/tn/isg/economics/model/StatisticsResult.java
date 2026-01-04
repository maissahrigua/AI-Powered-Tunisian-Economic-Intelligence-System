package tn.isg.economics.model;

public record StatisticsResult(
        int count,
        double sum,
        double mean,
        double median,
        double min,
        double max,
        double range,
        double variance,
        double stdDev,
        double q1,
        double q3
) {
    public StatisticsResult {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
    }

    public String toFormattedString() {
        return String.format("""
            === STATISTICS SUMMARY ===
            Count:              %d
            Sum:                %.2f
            Mean (Average):     %.2f
            Median:             %.2f
            Min:                %.2f
            Max:                %.2f
            Range:              %.2f
            Std Deviation:      %.2f
            Variance:           %.2f
            Q1 (25th %%ile):     %.2f
            Q3 (75th %%ile):     %.2f
            ==========================
            """,
                count, sum, mean, median, min, max, range, stdDev, variance, q1, q3
        );
    }

    public static StatisticsResult empty() {
        return new StatisticsResult(0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public double getCoefficientOfVariation() {
        if (mean == 0) {
            return 0.0;
        }
        return (stdDev / mean) * 100.0;
    }

    public double getInterquartileRange() {
        return q3 - q1;
    }
}