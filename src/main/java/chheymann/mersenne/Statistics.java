package chheymann.mersenne;


import java.util.Formatter;


/**
 * Used to track some stats and timing for the Mersenne checker
 */
public class Statistics {
    private int factorCount = 0;
    private int lucasLehmerCount = 0;
    private long totalFactoringTime = 0;
    private long totalLucasLehmerTime = 0;
    private long minP = Long.MAX_VALUE;
    private long maxP = Long.MIN_VALUE;

    public Statistics() {}

    public void searchedForFactor(long p, long factoringTime) {
        factorCount++;
        totalFactoringTime += factoringTime;
        updateMinMaxP(p);
    }

    public void perfomedLucasLehmer(long p, long lucasLehmerTime) {
        lucasLehmerCount++;
        totalLucasLehmerTime += lucasLehmerTime;
        updateMinMaxP(p);
    }

    private void updateMinMaxP(long p) {
        if (p > maxP) {
            maxP = p;
        }
        if (p < minP) {
            minP = p;
        }
    }

    @Override
    public String toString() {
        int factoredOnly = factorCount - lucasLehmerCount;
        double factorOnlyPercentage = (double) factoredOnly / (double) factorCount * 100d;
        long averageTimeFactoring = (long) ((double) totalFactoringTime / (double) factorCount);
        long averageTimeLucasLehmer = (long) ((double) totalLucasLehmerTime / (double) lucasLehmerCount);
        StringBuilder b = new StringBuilder();
        try (Formatter formatter = new Formatter(b)) {
            formatter
                    .format("Between exponents %d and %d, found small factor %.2f%% of the time, average factor time=%dms, average Lucas Lehmer time=%dms",
                            minP, maxP, factorOnlyPercentage, averageTimeFactoring, averageTimeLucasLehmer);
        }
        return b.toString();
    }

}
