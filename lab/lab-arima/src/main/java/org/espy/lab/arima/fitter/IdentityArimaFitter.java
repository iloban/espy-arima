package org.espy.lab.arima.fitter;

import org.espy.arima.ArimaProcess;
import org.espy.arima.DefaultArimaProcess;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;

import java.io.PrintWriter;

public final class IdentityArimaFitter implements ArimaFitter {

    @Override public boolean support(TimeSeriesSampleMetadata metadata) {
        return metadata instanceof ArimaTimeSeriesSampleMetadata;
    }

    @Override public ArimaProcess fit(TimeSeriesSampleMetadata metadata, double[] observations) {
        ArimaTimeSeriesSampleMetadata arimaMetadata = (ArimaTimeSeriesSampleMetadata) metadata;
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        arimaProcess.setArCoefficients(arimaMetadata.getArCoefficients());
        arimaProcess.setIntegrationOrder(arimaMetadata.getIntegrationOrder());
        arimaProcess.setMaCoefficients(arimaMetadata.getMaCoefficients());
        arimaProcess.setConstant(arimaMetadata.getConstant());
        arimaProcess.setShockExpectation(arimaMetadata.getShockExpectation());
        arimaProcess.setShockVariation(arimaMetadata.getShockVariation());
        return arimaProcess;
    }

    @Override public void write(PrintWriter writer) {
        writer.print("identity");
    }
}
