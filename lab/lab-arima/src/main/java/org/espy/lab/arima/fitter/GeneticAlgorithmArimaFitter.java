package org.espy.lab.arima.fitter;

import org.espy.arima.ArimaFitterStrategy;
import org.espy.arima.ArimaProcess;
import org.espy.arima.GeneticAlgorithmArimaFitterStrategy;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;

import java.io.PrintWriter;
import java.util.Random;

public final class GeneticAlgorithmArimaFitter implements ArimaFitter {

    private final Random random;

    private final GeneticAlgorithmArimaFitterStrategy.InnerArimaForecaster innerForecaster;

    public GeneticAlgorithmArimaFitter(Random random) {
        this(random, new GeneticAlgorithmArimaFitterStrategy.DefaultInnerArimaForecaster());
    }

    public GeneticAlgorithmArimaFitter(Random random,
                                       GeneticAlgorithmArimaFitterStrategy.InnerArimaForecaster innerForecaster) {
        this.random = random;
        this.innerForecaster = innerForecaster;
    }


    @Override public ArimaProcess fit(ArimaTimeSeriesSampleMetadata metadata, double[] observations) {
        ArimaFitterStrategy fitter = new GeneticAlgorithmArimaFitterStrategy(observations, random, innerForecaster);
        return fitter.fit();
    }

    @Override public void write(PrintWriter writer) {
        writer.print("genetic algorithm with forecaster: " + innerForecaster);
    }
}
