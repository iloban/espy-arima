package org.espy.lab.arima.generator;

import org.espy.lab.arima.generator.coefficient.ArimaCoefficientsGenerator;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;
import org.espy.lab.generator.GeneratorContext;
import org.espy.lab.generator.TimeSeriesGenerator;
import org.espy.lab.sample.TimeSeriesSample;

import java.util.Random;

public class ArimaGenerator implements TimeSeriesGenerator {

    private final int p;

    private final int d;

    private final int q;

    private final int observedPartLength;

    private final int unobservedPartLength;

    private final ArimaCoefficientsGenerator coefficientsGenerator;

    private final ArimaGeneratorEngine generatorEngine;

    public ArimaGenerator(int p, int d, int q,
                          int observedPartLength, int unobservedPartLength,
                          ArimaCoefficientsGenerator coefficientsGenerator,
                          ArimaGeneratorEngine generatorEngine) {
        // TODO: 4/2/2017 add preconditions
        this.p = p;
        this.d = d;
        this.q = q;
        this.observedPartLength = observedPartLength;
        this.unobservedPartLength = unobservedPartLength;
        this.coefficientsGenerator = coefficientsGenerator;
        this.generatorEngine = generatorEngine;
    }

    @Override public TimeSeriesSample generate(GeneratorContext generatorContext) {
        Random random = generatorContext.getRandom();
        double[] arCoefficients = coefficientsGenerator.generateArCoefficients(p, random);
        double[] maCoefficients = coefficientsGenerator.generateMaCoefficients(q, random, arCoefficients);
        ArimaTimeSeriesSampleMetadata metadata = new ArimaTimeSeriesSampleMetadata(
                arCoefficients,
                d,
                maCoefficients,
                observedPartLength,
                unobservedPartLength
        );
        GeneratedParts parts = generatorEngine.generate(
                arCoefficients,
                d,
                maCoefficients,
                observedPartLength,
                unobservedPartLength,
                random
        );
        return new TimeSeriesSample(metadata, parts.observedPart, parts.unobservedPart);
    }

    public interface ArimaGeneratorEngine {

        GeneratedParts generate(double[] arCoefficients, int d, double[] maCoefficients,
                                int observedPartLength, int unobservedPartLength,
                                Random random);
    }

    public static final class GeneratedParts {

        public final double[] observedPart;

        public final double[] unobservedPart;

        public GeneratedParts(double[] observedPart, double[] unobservedPart) {
            this.observedPart = observedPart;
            this.unobservedPart = unobservedPart;
        }
    }
}
