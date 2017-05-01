package org.espy.lab.arima.generator.coefficient;

import java.util.Random;

public interface ArimaCoefficientsGenerator {

    double[] generateArCoefficients(int p, Random random);

    double[] generateMaCoefficients(int q, Random random, double[] arCoefficients);

    double generateConstant(Random random);

    double generateShockExpectation(Random random);

    double generateShockVariation(Random random);
}
