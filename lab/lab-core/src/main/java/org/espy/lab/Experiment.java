package org.espy.lab;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class Experiment {

    private final File suiteFile;

    private final TimeSeriesProcessorReportAggregator aggregator;

    private final List<TimeSeriesProcessor> processors;

    public Experiment(String suiteFileName,
                      TimeSeriesProcessorReportAggregator aggregator,
                      TimeSeriesProcessor... processors) {
        suiteFile = new File(suiteFileName);
        if (!suiteFile.exists()) {
            throw new IllegalArgumentException("File not found: " + suiteFileName);
        }
        this.aggregator = aggregator;
        this.processors = Arrays.asList(processors);
    }

    public ExperimentReport run() {
        init();
        TimeSeriesSuite suite = readTimeSeriesSuite();
        ExperimentReport.Builder builder = ExperimentReport.builder()
                .setTimeSeriesSuiteFileName(suiteFile.getAbsolutePath())
                .setTimeSeriesProcessorReportAggregator(aggregator);
        for (TimeSeriesProcessor processor : processors) {
            for (TimeSeriesSample sample : suite) {
                builder.putTimeSeriesProcessorReport(processor, processor.run(sample));
            }
        }
        return builder.build();
    }

    private void init() {
        for (TimeSeriesProcessor processor : processors) {
            processor.init();
        }
    }

    private TimeSeriesSuite readTimeSeriesSuite() {
        try (Scanner scanner = new Scanner(suiteFile)) {
            return TimeSeriesSuite.read(scanner);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Can't read a time series suite", e);
        }
    }
}
