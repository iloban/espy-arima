package org.espy.lab.experiment;

import org.espy.lab.processor.TimeSeriesProcessor;
import org.espy.lab.report.ExperimentReport;
import org.espy.lab.report.TimeSeriesProcessorReport;
import org.espy.lab.report.TimeSeriesProcessorReportAggregator;
import org.espy.lab.report.util.UnsupportedSampleProcessorReport;
import org.espy.lab.sample.TimeSeriesSample;
import org.espy.lab.suite.TimeSeriesSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public final class Experiment<R extends TimeSeriesProcessorReport> {

    private final File suiteFile;

    private final TimeSeriesProcessorReportAggregator<R> aggregator;

    private final List<TimeSeriesProcessor<R>> processors;

    public Experiment(String suiteFileName,
                      TimeSeriesProcessorReportAggregator<R> aggregator,
                      List<TimeSeriesProcessor<R>> processors) {
        suiteFile = new File(suiteFileName);
        if (!suiteFile.exists()) {
            throw new IllegalArgumentException("File not found: " + suiteFileName);
        }
        this.aggregator = aggregator;
        this.processors = processors;
    }

    public ExperimentReport<R> run() {
        init();
        TimeSeriesSuite suite = readTimeSeriesSuite();
        ExperimentReport.Builder<R> builder = ExperimentReport.<R>builder()
                .setTimeSeriesSuiteFileName(suiteFile.getAbsolutePath())
                .setTimeSeriesProcessorReportAggregator(aggregator);
        for (TimeSeriesProcessor<R> processor : processors) {
            for (TimeSeriesSample sample : suite) {
                if (processor.support(sample)) {
                    R report = processor.process(sample);
                    builder.putTimeSeriesProcessorReport(processor, report);
                } else {
                    TimeSeriesProcessorReport report = new UnsupportedSampleProcessorReport(processor, sample);
                    builder.putTimeSeriesProcessorErrorReport(processor, report);
                }
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