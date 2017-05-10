package org.espy.lab.arima.fitter;

import org.espy.arima.ArimaFitterStrategy;
import org.espy.arima.ArimaProcess;
import org.espy.arima.MethodOfMomentsArimaFitterStrategy;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;

import java.io.PrintWriter;

public final class MethodOfMomentsArimaFitter implements ArimaFitter {

    @Override public boolean support(TimeSeriesSampleMetadata metadata) {
        return metadata instanceof ArimaTimeSeriesSampleMetadata;
    }

    @Override public ArimaProcess fit(TimeSeriesSampleMetadata metadata, double[] observations) {
        ArimaTimeSeriesSampleMetadata arimaMetadata = (ArimaTimeSeriesSampleMetadata) metadata;
        ArimaFitterStrategy fitter = new MethodOfMomentsArimaFitterStrategy(
                observations,
                arimaMetadata.getArCoefficients().length,
                arimaMetadata.getMaCoefficients().length,
                arimaMetadata.getIntegrationOrder()
        );
        return fitter.fit();
    }

    @Override public void write(PrintWriter writer) {
        writer.print("method of moments");
    }
}
