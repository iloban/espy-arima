package org.espy.lab.arima.generator;

import org.espy.arima.ArimaProcessRealization;
import org.espy.arima.DefaultArimaProcess;
import org.espy.arima.DefaultArimaProcessRealization;

import java.util.Random;

public final class DefaultArimaGeneratorEngine implements ArimaGenerator.ArimaGeneratorEngine {

    @Override public ArimaGenerator.GeneratedParts generate(double[] arCoefficients, int d, double[] maCoefficients,
                                                            int observedPartLength, int unobservedPartLength,
                                                            Random random) {
        DefaultArimaProcess process = new DefaultArimaProcess();
        process.setArCoefficients(arCoefficients);
        process.setIntegrationOrder(d);
        process.setMaCoefficients(maCoefficients);
        ArimaProcessRealization realization = new DefaultArimaProcessRealization(process, random);
        return new ArimaGenerator.GeneratedParts(
                realization.next(observedPartLength),
                realization.next(unobservedPartLength)
        );
    }
}
