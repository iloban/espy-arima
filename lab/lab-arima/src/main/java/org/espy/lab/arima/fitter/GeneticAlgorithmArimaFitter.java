package org.espy.lab.arima.fitter;

import org.espy.arima.ArimaFitterStrategy;
import org.espy.arima.ArimaProcess;
import org.espy.arima.GeneticAlgorithmArimaFitterStrategy;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;

import java.io.PrintWriter;
import java.util.Random;
import java.util.function.Supplier;

public final class GeneticAlgorithmArimaFitter implements ArimaFitter {

    private final Supplier<Random> randomSupplier;

    private final GeneticAlgorithmArimaFitterStrategy.InnerArimaForecaster innerForecaster;

    public GeneticAlgorithmArimaFitter(Supplier<Random> randomSupplier) {
        this(randomSupplier, new GeneticAlgorithmArimaFitterStrategy.DefaultInnerArimaForecaster());
    }

    public GeneticAlgorithmArimaFitter(Supplier<Random> randomSupplier,
                                       GeneticAlgorithmArimaFitterStrategy.InnerArimaForecaster innerForecaster) {
        this.randomSupplier = randomSupplier;
        this.innerForecaster = innerForecaster;
    }


    @Override public ArimaProcess fit(ArimaTimeSeriesSampleMetadata metadata, double[] observations) {
        ArimaFitterStrategy fitter = new GeneticAlgorithmArimaFitterStrategy(observations,
                metadata.getUnobservedPartLength(), randomSupplier.get(), innerForecaster);
        return fitter.fit();
    }

    @Override public void write(PrintWriter writer) {
        writer.print("genetic algorithm with forecaster: " + innerForecaster);
    }
}
