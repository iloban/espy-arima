package org.espy.lab.arima.generator.coefficient;

import java.util.Random;

public final class DefaultArimaCoefficientsGenerator implements ArimaCoefficientsGenerator {

    @Override public double[] generateArCoefficients(int p, Random random) {
        return generateCoefficients(p, random);
    }

    private static double[] generateCoefficients(int count, Random random) {
        double[] coefficients = new double[count];
        for (int i = 0; i < count; i++) {
            coefficients[i] = Math.round(random.nextDouble() * 1_000) / 1_000.0;
        }
        return coefficients;
    }

    @Override public double[] generateMaCoefficients(int q, Random random, double[] arCoefficients) {
        return generateCoefficients(q, random);
    }
}
