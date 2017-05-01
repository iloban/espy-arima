package org.espy.lab.arima.generator.engine;

import org.espy.arima.ArimaProcessRealization;
import org.espy.arima.DefaultArimaProcess;
import org.espy.arima.DefaultArimaProcessRealization;
import org.espy.lab.arima.generator.ArimaGenerator;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;

import java.util.Random;

public final class DefaultArimaGeneratorEngine implements ArimaGenerator.ArimaGeneratorEngine {

    @Override public ArimaGenerator.GeneratedParts generate(ArimaTimeSeriesSampleMetadata metadata, Random random) {
        DefaultArimaProcess process = new DefaultArimaProcess();
        process.setArCoefficients(metadata.getArCoefficients());
        process.setIntegrationOrder(metadata.getIntegrationOrder());
        process.setMaCoefficients(metadata.getMaCoefficients());
        process.setConstant(metadata.getConstant());
        process.setShockExpectation(metadata.getShockExpectation());
        process.setShockVariation(metadata.getShockVariation());
        ArimaProcessRealization realization = new DefaultArimaProcessRealization(process, random);
        return new ArimaGenerator.GeneratedParts(
                realization.next(metadata.getObservedPartLength()),
                realization.next(metadata.getUnobservedPartLength())
        );
    }
}
