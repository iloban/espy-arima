package org.espy.lab.examples;

import org.espy.lab.arima.generator.ArimaGenerators;
import org.espy.lab.suite.TimeSeriesSuite;
import org.espy.lab.suite.TimeSeriesSuiteConfiguration;
import org.espy.lab.suite.TimeSeriesSuiteFactory;
import org.espy.lab.util.WritableUtils;

import java.io.FileNotFoundException;

public class SuiteGeneration {

    public static void main(String[] args) throws FileNotFoundException {

        int index = 1;
        int seed = 1;
        String rootDir = "lab/lab-examples/src/main/resources";

        TimeSeriesSuiteConfiguration configuration = TimeSeriesSuiteConfiguration.builder()
                .setSeed(seed)
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

        WritableUtils.saveInFile(suite, rootDir + "/test-suites", "suite_" + index + ".txt");
    }
}
