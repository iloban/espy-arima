package org.espy.lab.arima.generator;

import org.espy.arima.ArimaProcessRealization;
import org.espy.arima.DefaultArimaProcess;
import org.espy.arima.DefaultArimaProcessRealization;
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

    public ArimaGenerator(int p, int d, int q,
                          int observedPartLength, int unobservedPartLength,
                          ArimaCoefficientsGenerator coefficientsGenerator) {
        // TODO: 4/2/2017 add preconditions
        this.p = p;
        this.d = d;
        this.q = q;
        this.observedPartLength = observedPartLength;
        this.unobservedPartLength = unobservedPartLength;
        this.coefficientsGenerator = coefficientsGenerator;
    }

    @Override public TimeSeriesSample generate(GeneratorContext generatorContext) {
        Random random = generatorContext.getRandom();
        DefaultArimaProcess process = new DefaultArimaProcess();
        double[] arCoefficients = coefficientsGenerator.generateArCoefficients(p, random);
        process.setArCoefficients(arCoefficients);
        process.setIntegrationOrder(d);
        process.setMaCoefficients(coefficientsGenerator.generateMaCoefficients(q, random, arCoefficients));
        ArimaProcessRealization realization = new DefaultArimaProcessRealization(process, random);
        ArimaTimeSeriesSampleMetadata metadata = new ArimaTimeSeriesSampleMetadata(
                realization.getArCoefficients(),
                d,
                realization.getMaCoefficients(),
                observedPartLength,
                unobservedPartLength
        );
        return new TimeSeriesSample(
                metadata,
                realization.next(observedPartLength),
                realization.next(unobservedPartLength)
        );
    }
}
