package org.espy.lab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import static org.espy.lab.GeneratorChoiceStrategyType.RANDOM;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
//        generateTimeSeriesSuite();
//        generateExperiment();
        runExperiment();
    }

    private static void runExperiment() throws FileNotFoundException {
        Experiment experiment;
        try (Scanner scanner = new Scanner(new File("lab/src/main/resources/experiments/experiment_1.txt"))) {
            experiment = Experiment.unmarshal(scanner);
        }
        ExperimentReport report = experiment.run();
        try (PrintWriter writer = new PrintWriter("lab/src/main/resources/reports/report_1.txt")) {
            report.marshal(writer);
        }
    }

    private static void generateTimeSeriesSuite() throws FileNotFoundException {
        TimeSeriesSuiteConfiguration configuration = TimeSeriesSuiteConfiguration.builder()
                .setSeed(1)
                .setGenerators(ArimaGenerators.natural(100))
                .setGeneratorUsageCount(3)
                .setChoiceStrategy(RANDOM)
                .build();
        TimeSeriesSuite suite = TimeSeriesSuiteFactory.createTimeSeriesSuite(configuration);
        try (PrintWriter writer = new PrintWriter("lab/src/main/resources/suites/suite_1.txt")) {
            suite.marshal(writer);
        }
    }

    private static void generateExperiment() throws FileNotFoundException {
        ExperimentConfiguration configuration = ExperimentConfiguration.builder()
                .setTimeSeriesSuiteFileName("lab/src/main/resources/suites/suite_1.txt")
                .setProcessors(new CheckArimaProcessor())
                .build();
        Experiment experiment = ExperimentFactory.createExperiment(configuration);
        try (PrintWriter writer = new PrintWriter("lab/src/main/resources/experiments/experiment_1.txt")) {
            experiment.marshal(writer);
        }
    }
}
