package org.espy.lab.arima.fitter;

import org.espy.arima.ArimaFitterStrategy;
import org.espy.arima.ArimaProcess;
import org.espy.arima.MethodOfMomentsArimaFitterStrategy;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;

import java.io.PrintWriter;

public final class MethodOfMomentsArimaFitter implements ArimaFitter {

    @Override public ArimaProcess fit(ArimaTimeSeriesSampleMetadata metadata, double[] observations) {
        ArimaFitterStrategy fitter = new MethodOfMomentsArimaFitterStrategy(
                observations,
                metadata.getArCoefficients().length,
                metadata.getMaCoefficients().length,
                metadata.getIntegrationOrder()
        );
        return fitter.fit();
    }

    @Override public void write(PrintWriter writer) {
        writer.print("method of moments");
    }
}
