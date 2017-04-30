package org.espy.lab.arima.fitter;

import org.espy.arima.ArimaFitterStrategy;
import org.espy.arima.ArimaProcess;
import org.espy.arima.GeneticAlgorithmArimaFitterStrategy;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;

import java.io.PrintWriter;

public final class GeneticAlgorithmArimaFitter implements ArimaFitter {

    @Override public ArimaProcess fit(ArimaTimeSeriesSampleMetadata metadata, double[] observations) {
        ArimaFitterStrategy fitter = new GeneticAlgorithmArimaFitterStrategy(observations);
        return fitter.fit();
    }

    @Override public void write(PrintWriter writer) {
        writer.print("genetic algorithm");
    }
}
