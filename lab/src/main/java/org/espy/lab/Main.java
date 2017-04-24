package org.espy.lab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.espy.lab.GeneratorChoiceStrategyType.RANDOM;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
//        generateTimeSeriesSuite();
//        generateExperiment();
//        runExperiment();
    }

    private static void generateTimeSeriesSuite() throws FileNotFoundException {
        TimeSeriesSuiteConfiguration configuration = TimeSeriesSuiteConfiguration.builder()
                .setSeed(1)
                .setGenerators(ArimaGenerators.natural(90, 10))
                .setGeneratorUsageCount(3)
                .setChoiceStrategy(RANDOM)
                .build();
        TimeSeriesSuite suite = TimeSeriesSuiteFactory.createTimeSeriesSuite(configuration);
        try (PrintWriter writer = new PrintWriter("lab/src/main/resources/suites/suite_1.txt")) {
            suite.write(writer);
        }
    }
}
