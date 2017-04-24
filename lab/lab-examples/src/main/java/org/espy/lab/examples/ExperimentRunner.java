package org.espy.lab.examples;

import org.espy.lab.*;
import org.espy.lab.arima.ArimaForecaster;
import org.espy.lab.arima.ArimaGenerators;
import org.espy.lab.arima.ArimaTimeSeriesProcessor;
import org.espy.lab.arima.IdentityArimaFitter;

import java.io.FileNotFoundException;

public class ExperimentRunner {

    public static void main(String[] args) throws FileNotFoundException {
        Experiment experiment = new Experiment(
                "lab/lab-examples/src/main/resources/suites/suite_1.txt",
                new FarmTimeSeriesProcessorReportAggregator(),
                new ArimaTimeSeriesProcessor(new IdentityArimaFitter(), new ArimaForecaster(), new ForecastComparator())
        );
        ExperimentReport report = experiment.run();
        WritableUtils.save(report, "lab/lab-examples/src/main/resources/reports", "report_1.txt");
    }

    private static void generateTimeSeriesSuite() throws FileNotFoundException {
        TimeSeriesSuiteConfiguration configuration = TimeSeriesSuiteConfiguration.builder()
                .setSeed(1)
                .setGenerators(ArimaGenerators.natural(40, 10))
                .setGeneratorUsageCount(10)
                .build();
        TimeSeriesSuite suite = TimeSeriesSuiteFactory.createTimeSeriesSuite(configuration);
        WritableUtils.save(suite, "lab/lab-examples/src/main/resources/suites", "suite_1.txt");
    }

    private static void runExperiment() throws FileNotFoundException {

    }
}
