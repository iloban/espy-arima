package org.espy.lab.arima.generator.coefficient;

import java.util.Random;

public final class DefaultArimaCoefficientsGenerator implements ArimaCoefficientsGenerator {

    private final double minAr;

    private final double maxAr;

    private final double minMa;

    private final double maxMa;

    public DefaultArimaCoefficientsGenerator() {
        this(0.05, 0.95, 0.01, 0.9);
    }

    public DefaultArimaCoefficientsGenerator(double minAr, double maxAr, double minMa, double maxMa) {
        this.minAr = minAr;
        this.maxAr = maxAr;
        this.minMa = minMa;
        this.maxMa = maxMa;
    }

    @Override public double[] generateArCoefficients(int p, Random random) {
        return generateCoefficients(p, random, minAr, maxAr);
    }

    private static double[] generateCoefficients(int count, Random random, double min, double max) {
        double[] coefficients = new double[count];
        if (count == 0) {
            return coefficients;
        }
        coefficients[0] = generateCoefficient(random, min, max);
        for (int i = 1; i < count; i++) {
            double maxi = 0.99;
            for (int j = 0; j < i; j++) {
                maxi -= Math.abs(coefficients[j]);
            }
            if (maxi < 0.05) {
                maxi = 0.05;
            }
            if (maxi > Math.abs(coefficients[i - 1])) {
                maxi = Math.abs(coefficients[i - 1]);
            }
            double mini = maxi + 0.01 > min ? min : 0.01;
            coefficients[i] = generateCoefficient(random, mini, maxi);
        }
        return coefficients;
    }

    private static double generateCoefficient(Random random, double min, double max) {
        return (random.nextBoolean() ? 1 : -1)
                * Math.round(((max - min) * random.nextDouble() + min) * 1_000) / 1_000.0;
    }

    @Override public double[] generateMaCoefficients(int q, Random random, double[] arCoefficients) {
        return generateCoefficients(q, random, minMa, maxMa);
    }

    @Override public double generateConstant(Random random) {
        return 0;
    }

    @Override public double generateShockExpectation(Random random) {
        return 0;
    }

    @Override public double generateShockVariation(Random random) {
        return 1;
    }
}
