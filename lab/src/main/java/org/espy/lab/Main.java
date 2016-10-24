package org.espy.lab;

import java.io.PrintWriter;

import static org.espy.lab.ChoiceStrategyType.RANDOM;
import static org.espy.lab.TimeSeriesSuiteConfiguration.builder;

public class Main {

    public static void main(String[] args) {
        TimeSeriesSuiteConfiguration configuration = builder()
                .setSeed(1)
                .setGenerators(ArimaGenerators.natural())
                .setGeneratorUsageCount(3)
                .setChoiceStrategyType(RANDOM)
                .build();
        TimeSeriesSuite suite = TimeSeriesSuiteFactory.generate(configuration);
        suite.marshal(new PrintWriter(System.out));
    }
}
