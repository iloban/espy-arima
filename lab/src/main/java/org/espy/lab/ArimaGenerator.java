package org.espy.lab;

import org.espy.arima.ArimaProcessRealization;
import org.espy.arima.DefaultArimaProcess;
import org.espy.arima.DefaultArimaProcessRealization;

import java.util.Random;

public class ArimaGenerator implements TimeSeriesGenerator {

    private final int p;

    private final int d;

    private final int q;

    public ArimaGenerator(int p, int d, int q) {
        this.p = p;
        this.d = d;
        this.q = q;
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
                realization.getMaCoefficients()
        );
        return new TimeSeriesSample(metadata, realization.next(100));
    }

    private double[] generateCoefficients(int count, Random random) {
        double[] coefficients = new double[count];
        for (int i = 0; i < count; i++) {
            coefficients[i] = Math.round(random.nextDouble() * 1_000) / 1_000.0;
        }
        return coefficients;
    }
}
