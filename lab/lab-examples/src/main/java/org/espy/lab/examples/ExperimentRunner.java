package org.espy.lab.examples;

import org.espy.lab.arima.fitter.IdentityArimaFitter;
import org.espy.lab.arima.forecast.EspyNaiveMultiStepAheadArimaForecaster;
import org.espy.lab.arima.forecast.EspyNaiveOneStepAheadArimaForecaster;
import org.espy.lab.arima.processor.ArimaTimeSeriesProcessor;
import org.espy.lab.experiment.Experiment;
import org.espy.lab.experiment.ExperimentResult;
import org.espy.lab.forecast.farm.FarmForecastComparator;
import org.espy.lab.report.farm.FarmTimeSeriesProcessorReportAggregator;
import org.espy.lab.util.WritableUtils;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class ExperimentRunner {

    public static void main(String[] args) throws FileNotFoundException {

        int i = 1;
        String rootDir = "lab/lab-examples/src/main/resources";

        Experiment experiment = new Experiment<>(
                rootDir + "/test-suites/suite_" + i + ".txt",
                new FarmTimeSeriesProcessorReportAggregator(),
                Arrays.asList(
                        new ArimaTimeSeriesProcessor<>(
                                new IdentityArimaFitter(),
                                new EspyNaiveOneStepAheadArimaForecaster(),
                                new FarmForecastComparator()
                        ),
                        new ArimaTimeSeriesProcessor<>(
                                new IdentityArimaFitter(),
                                new EspyNaiveMultiStepAheadArimaForecaster(),
                                new FarmForecastComparator()
                        )
                )
        );

        ExperimentResult result = experiment.run();

        WritableUtils.save(result.getShortReport(), rootDir + "/reports", "report_" + i + ".txt");
    }
}
