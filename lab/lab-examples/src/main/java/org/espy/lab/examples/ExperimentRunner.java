package org.espy.lab.examples;

import org.espy.lab.arima.fitter.GeneticAlgorithmArimaFitter;
import org.espy.lab.arima.fitter.IdentityArimaFitter;
import org.espy.lab.arima.forecast.EspyNaiveMultiStepAheadArimaForecaster;
import org.espy.lab.arima.processor.ArimaTimeSeriesProcessor;
import org.espy.lab.experiment.Experiment;
import org.espy.lab.experiment.ExperimentResult;
import org.espy.lab.forecast.farm.FarmForecastComparator;
import org.espy.lab.report.farm.FarmTimeSeriesProcessorReportAggregator;
import org.espy.lab.util.WritableUtils;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;

public class ExperimentRunner {

    public static void main(String[] args) throws FileNotFoundException {

        int index = 1;
        int seed = 1;
        String rootDir = "lab/lab-examples/src/main/resources";

        Experiment experiment = new Experiment<>(
                rootDir + "/test-suites/suite_" + index + ".txt",
                new FarmTimeSeriesProcessorReportAggregator(),
                Arrays.asList(
                        new ArimaTimeSeriesProcessor<>(
                                new IdentityArimaFitter(),
                                new EspyNaiveMultiStepAheadArimaForecaster(),
                                new FarmForecastComparator()
                        ),
                        new ArimaTimeSeriesProcessor<>(
                                new GeneticAlgorithmArimaFitter(() -> new Random(seed)),
                                new EspyNaiveMultiStepAheadArimaForecaster(),
                                new FarmForecastComparator()
                        )
                ),
                true
        );

        ExperimentResult result = experiment.run();

        WritableUtils.saveInFile(result.getShortReport(), rootDir + "/reports", "report_" + index + ".txt");
    }
}
