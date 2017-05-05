package org.espy.lab.examples;

import org.espy.lab.arima.generator.ArimaGenerators;
import org.espy.lab.suite.TimeSeriesSuite;
import org.espy.lab.suite.TimeSeriesSuiteConfiguration;
import org.espy.lab.suite.TimeSeriesSuiteFactory;
import org.espy.lab.util.WritableUtils;

import java.io.FileNotFoundException;

public class TimeSeriesSuiteGeneration {

    public static void main(String[] args) throws FileNotFoundException {

        int i = 2;
        String rootDir = "lab/lab-examples/src/main/resources";

        TimeSeriesSuiteConfiguration configuration = TimeSeriesSuiteConfiguration.builder()
                .setSeed(i)
                .setGenerators(ArimaGenerators
                        .natural(
                                0.5, 1,
                                0.02, 0.4,
                                0.5, 1.5
                        )
                        .build()
                )
                .setGeneratorUsageCount(1000)
                .build();

        TimeSeriesSuite suite = TimeSeriesSuiteFactory.createTimeSeriesSuite(configuration);

        WritableUtils.save(suite, rootDir + "/test-suites", "suite_" + i + ".txt");
    }
}
