package org.espy.lab.experiment;

import org.espy.lab.processor.TimeSeriesProcessor;
import org.espy.lab.report.TimeSeriesProcessorReport;
import org.espy.lab.report.TimeSeriesProcessorReportAggregator;
import org.espy.lab.report.util.UnsupportedSampleProcessorReport;
import org.espy.lab.sample.TimeSeriesSample;
import org.espy.lab.suite.TimeSeriesSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public final class Experiment<R extends TimeSeriesProcessorReport> {

    private final File suiteFile;

    private final TimeSeriesProcessorReportAggregator<R> aggregator;

    private final List<TimeSeriesProcessor<R>> processors;

    private final ProgressTracker progressTracker;

    public Experiment(String suiteFileName,
                      TimeSeriesProcessorReportAggregator<R> aggregator,
                      List<TimeSeriesProcessor<R>> processors) {
        this(suiteFileName, aggregator, processors, false);
    }

    public Experiment(String suiteFileName,
                      TimeSeriesProcessorReportAggregator<R> aggregator,
                      List<TimeSeriesProcessor<R>> processors,
                      boolean trackProgress) {
        this.suiteFile = new File(suiteFileName);
        this.aggregator = aggregator;
        this.processors = processors;
        this.progressTracker = trackProgress ? new PercentageAndTimeProgressTracker() : new DummyProgressTracker();
    }

    public ExperimentResult<R> run() {
        for (TimeSeriesProcessor processor : processors) {
            processor.init();
        }
        TimeSeriesSuite suite = readTimeSeriesSuite(suiteFile);
        progressTracker.reset(suite.size());
        ExperimentResult.Builder<R> builder = ExperimentResult.<R>builder()
                .setTimeSeriesSuiteFileName(suiteFile.getAbsolutePath())
                .setTimeSeriesProcessorReportAggregator(aggregator);
        for (TimeSeriesSample sample : suite) {
            for (TimeSeriesProcessor<R> processor : processors) {
                if (!processor.support(sample)) {
                    TimeSeriesProcessorReport report = new UnsupportedSampleProcessorReport(processor, sample);
                    builder.putTimeSeriesProcessorErrorReport(processor, report);
                    continue;
                }
                try {
                    R report = processor.process(sample);
                    builder.putTimeSeriesProcessorReport(processor, report);
                } catch (Exception e) {
                    TimeSeriesProcessorReport report = new UnsupportedSampleProcessorReport(processor, sample, e);
                    builder.putTimeSeriesProcessorErrorReport(processor, report);
                }
            }
            progressTracker.onAfterSampleProcessing();
        }
        return builder.build();
    }

    private static TimeSeriesSuite readTimeSeriesSuite(File suiteFile) {
        try (Scanner scanner = new Scanner(suiteFile)) {
            return TimeSeriesSuite.read(scanner);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Can't read a time series suite", e);
        }
    }

    private interface ProgressTracker {

        void reset(int totalSamplesCount);

        void onAfterSampleProcessing();
    }

    private static final class DummyProgressTracker implements ProgressTracker {

        @Override public void reset(int totalSamplesCount) {
        }

        @Override public void onAfterSampleProcessing() {
        }
    }

    private static final class PercentageAndTimeProgressTracker implements ProgressTracker {

        private int totalSamplesCount;

        private int currentSamplesCount;

        private int currentProgress;

        private Instant lastIterationTimestamp;

        private Duration averageIterationDuration;

        private Long previousRemainerInSeconds;

        @Override public void reset(int totalSamplesCount) {
            this.totalSamplesCount = totalSamplesCount;
            this.currentSamplesCount = 0;
            this.currentProgress = -1;
            this.lastIterationTimestamp = Instant.now();
            this.averageIterationDuration = null;
            this.previousRemainerInSeconds = null;
        }

        @Override public void onAfterSampleProcessing() {
            updateAverageIterationDuration();
            int newProgress = calcNewProgress();
            if (newProgress > currentProgress) {
                currentProgress = newProgress;
                Duration remainder = averageIterationDuration.multipliedBy(totalSamplesCount - currentSamplesCount);
                long seconds = remainder.getSeconds();
                if (previousRemainerInSeconds == null || previousRemainerInSeconds != seconds) {
                    previousRemainerInSeconds = seconds;
                    if (remainder.compareTo(ChronoUnit.MINUTES.getDuration()) >= 0) {
                        int minutes = (int) Math.floor(seconds / 60.0);
                        System.out.println(currentProgress + "%\t| ~" + minutes + " minutes " + seconds % 60 + " seconds");
                    } else {
                        System.out.println(currentProgress + "%\t| ~" + (seconds < 59 ? seconds + 1 : seconds) + " seconds");
                    }
                } else if (currentProgress == 100) {
                    System.out.println("100%");
                }
            }
        }

        private void updateAverageIterationDuration() {
            Instant now = Instant.now();
            Duration currentIterationDuration = Duration.between(lastIterationTimestamp, now);
            lastIterationTimestamp = now;
            if (averageIterationDuration == null) {
                averageIterationDuration = currentIterationDuration;
            } else {
                long averageIterationDurationInNanos = Math.round(
                        (double) (averageIterationDuration.toNanos() * currentSamplesCount
                                + currentIterationDuration.toNanos())
                                / (currentSamplesCount + 1)
                );
                averageIterationDuration = Duration.ofNanos(averageIterationDurationInNanos);
            }
        }

        private int calcNewProgress() {
            double ratio = (double) ++currentSamplesCount / totalSamplesCount;
            return (int) Math.min(Math.round(ratio * 100), 100);
        }
    }
}
