package org.espy.lab.arima.generator;

import org.espy.lab.arima.generator.coefficient.ArimaCoefficientsGenerator;
import org.espy.lab.arima.generator.coefficient.DefaultArimaCoefficientsGenerator;
import org.espy.lab.arima.generator.engine.DefaultArimaGeneratorEngine;
import org.espy.lab.generator.TimeSeriesGenerator;

import java.util.ArrayList;
import java.util.List;

public final class ArimaGenerators {

    public static Builder natural() {
        return new Builder();
    }

    public static Builder natural(double minAr, double maxAr, double minMa, double maxMa) {
        return natural(minAr, maxAr, minMa, maxMa, 1, 1);
    }

    public static Builder natural(double minAr, double maxAr, double minMa, double maxMa, double minShockVariation, double maxShockVariation) {
        ArimaCoefficientsGenerator coefficientsGenerator = new DefaultArimaCoefficientsGenerator(
                minAr, maxAr,
                minMa, maxMa,
                minShockVariation, maxShockVariation
        );
        return new Builder().setCoefficientsGenerator(coefficientsGenerator);
    }

    public static final class Builder {

        private int minP = 0;
        private int maxP = 2;

        private int minD = 0;
        private int maxD = 2;

        private int minQ = 0;
        private int maxQ = 2;

        private ArimaCoefficientsGenerator coefficientsGenerator;

        private ArimaGenerator.ArimaGeneratorEngine generatorEngine;

        public List<TimeSeriesGenerator> build() {
            List<TimeSeriesGenerator> generators = new ArrayList<>();
            for (int d = minD; d <= maxD; d++) {
                for (int q = minQ; q <= maxQ; q++) {
                    for (int p = minP; p <= maxP; p++) {
                        if (p != 0 && q != 0) {
                            generators.add(createArimaGenerator(d, q, p));
                        }
                    }
                }
            }
            return generators;
        }

        private ArimaGenerator createArimaGenerator(int d, int q, int p) {
            if (coefficientsGenerator == null) {
                coefficientsGenerator = new DefaultArimaCoefficientsGenerator();
            }
            if (generatorEngine == null) {
                generatorEngine = new DefaultArimaGeneratorEngine();
            }
            return new ArimaGenerator(
                    p, d, q,
                    coefficientsGenerator,
                    generatorEngine
            );
        }

        public Builder setCoefficientsGenerator(ArimaCoefficientsGenerator coefficientsGenerator) {
            this.coefficientsGenerator = coefficientsGenerator;
            return this;
        }

        public Builder setGeneratorEngine(ArimaGenerator.ArimaGeneratorEngine generatorEngine) {
            this.generatorEngine = generatorEngine;
            return this;
        }
    }
}
