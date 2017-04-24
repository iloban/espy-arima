package org.espy.lab.examples;

import org.espy.lab.arima.generator.ArimaGenerators;
import org.espy.lab.suite.TimeSeriesSuite;
import org.espy.lab.suite.TimeSeriesSuiteConfiguration;
import org.espy.lab.suite.TimeSeriesSuiteFactory;
import org.espy.lab.util.WritableUtils;

import java.io.FileNotFoundException;

public class TimeSeriesSuiteGeneration {

    public static void main(String[] args) throws FileNotFoundException {
        TimeSeriesSuiteConfiguration configuration = TimeSeriesSuiteConfiguration.builder()
                .setSeed(1)
                .setGenerators(ArimaGenerators.natural(40, 10))
                .setGeneratorUsageCount(10)
                .build();
        TimeSeriesSuite suite = TimeSeriesSuiteFactory.createTimeSeriesSuite(configuration);
        WritableUtils.save(suite, "lab/lab-examples/src/main/resources/suites", "suite_1.txt");
    }
}
