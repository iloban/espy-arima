package org.espy.lab.examples;

import org.espy.lab.arima.fitter.IdentityArimaFitter;
import org.espy.lab.arima.forecast.ArimaForecaster;
import org.espy.lab.arima.processor.ArimaTimeSeriesProcessor;
import org.espy.lab.experiment.Experiment;
import org.espy.lab.forecast.farm.FarmForecastComparator;
import org.espy.lab.report.ExperimentReport;
import org.espy.lab.report.farm.FarmTimeSeriesProcessorReportAggregator;
import org.espy.lab.util.WritableUtils;

import java.io.FileNotFoundException;
import java.util.Collections;

public class ExperimentRunner {

    public static void main(String[] args) throws FileNotFoundException {
        Experiment experiment = new Experiment<>(
                "lab/lab-examples/src/main/resources/suites/suite_1.txt",
                new FarmTimeSeriesProcessorReportAggregator(),
                Collections.singletonList(new ArimaTimeSeriesProcessor<>(
                        new IdentityArimaFitter(),
                        new ArimaForecaster(),
                        new FarmForecastComparator()
                ))
        );
        ExperimentReport report = experiment.run();
        WritableUtils.save(report, "lab/lab-examples/src/main/resources/reports", "report_1.txt");
    }
}
