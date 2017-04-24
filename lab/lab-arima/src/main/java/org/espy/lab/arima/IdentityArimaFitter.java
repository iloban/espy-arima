package org.espy.lab.arima;

import org.espy.arima.ArimaProcess;
import org.espy.arima.DefaultArimaProcess;

import java.io.PrintWriter;

public final class IdentityArimaFitter implements ArimaFitter {

    @Override public ArimaProcess fit(ArimaTimeSeriesSampleMetadata metadata, double[] values) {
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        arimaProcess.setArCoefficients(metadata.getArCoefficients());
        arimaProcess.setIntegrationOrder(metadata.getIntegrationOrder());
        arimaProcess.setMaCoefficients(metadata.getMaCoefficients());
        return arimaProcess;
    }

    @Override public void write(PrintWriter writer) {
        writer.print("identity ARIMA fitter");
    }
}
