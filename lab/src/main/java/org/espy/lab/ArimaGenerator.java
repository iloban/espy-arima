package org.espy.lab;

import org.espy.arima.ArimaProcessRealization;
import org.espy.arima.DefaultArimaProcess;
import org.espy.arima.DefaultArimaProcessRealization;

import java.util.Random;

public class ArimaGenerator implements TimeSeriesGenerator {

    private final int p;

    private final int d;

    private final int q;

    private final int observedPartLength;

    private final int unobservedPartLength;

    public ArimaGenerator(int p, int d, int q, int observedPartLength, int unobservedPartLength) {
        // TODO: 4/2/2017 add preconditions
        this.p = p;
        this.d = d;
        this.q = q;
        this.observedPartLength = observedPartLength;
        this.unobservedPartLength = unobservedPartLength;
    }

    @Override public TimeSeriesSample generate(GeneratorContext generatorContext) {
        Random random = generatorContext.getRandom();
        DefaultArimaProcess process = new DefaultArimaProcess();
        process.setArCoefficients(generateCoefficients(p, random));
        process.setIntegrationOrder(d);
        process.setMaCoefficients(generateCoefficients(q, random));
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

    private double[] generateCoefficients(int count, Random random) {
        double[] coefficients = new double[count];
        for (int i = 0; i < count; i++) {
            coefficients[i] = Math.round(random.nextDouble() * 1_000) / 1_000.0;
        }
        return coefficients;
    }
}
