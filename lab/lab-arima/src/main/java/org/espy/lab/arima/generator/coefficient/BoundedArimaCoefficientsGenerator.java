package org.espy.lab.arima.generator.coefficient;

import java.util.Random;

public final class BoundedArimaCoefficientsGenerator implements ArimaCoefficientsGenerator {

    private final double minAr;

    private final double maxAr;

    private final double minMa;

    private final double maxMa;

    public BoundedArimaCoefficientsGenerator(double minAr, double maxAr, double minMa, double maxMa) {
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
        for (int i = 0; i < count; i++) {
            coefficients[i] = Math.round(((max - min) * random.nextDouble() + min) * 1_000) / 1_000.0;
        }
        return coefficients;
    }

    @Override public double[] generateMaCoefficients(int q, Random random, double[] arCoefficients) {
        return generateCoefficients(q, random, minMa, maxMa);
    }
}
