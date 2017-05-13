package org.espy.lab.experiment;

import org.espy.lab.processor.TimeSeriesProcessor;
import org.espy.lab.report.TimeSeriesProcessorReport;
import org.espy.lab.report.TimeSeriesProcessorReportAggregator;
import org.espy.lab.report.util.UnsupportedSampleReport;
import org.espy.lab.sample.TimeSeriesSample;
import org.espy.lab.suite.TimeSeriesSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Experiment<R extends TimeSeriesProcessorReport> {

    private final File suiteFile;

    private final TimeSeriesProcessorReportAggregator<R> aggregator;

    private final List<TimeSeriesProcessor<R>> processors;

    private final ProgressTracker progressTracker;

    private final ExecutorService executorService;

    private final boolean debugMode;

    public Experiment(String suiteFileName,
                      TimeSeriesProcessorReportAggregator<R> aggregator,
                      List<TimeSeriesProcessor<R>> processors) {
        this(suiteFileName, aggregator, processors, false, false);
    }

    public Experiment(String suiteFileName,
                      TimeSeriesProcessorReportAggregator<R> aggregator,
                      List<TimeSeriesProcessor<R>> processors,
                      boolean trackProgress,
                      boolean debugMode) {
        this.suiteFile = new File(suiteFileName);
        this.aggregator = aggregator;
        this.processors = processors;
        int threadCount = debugMode ? 1 : Runtime.getRuntime().availableProcessors();
        this.progressTracker = trackProgress ? new PercentageAndTimeProgressTracker(threadCount) : new DummyProgressTracker();
        this.executorService = createDefaultThreadPool(threadCount);
        this.debugMode = debugMode;
    }

    private static ExecutorService createDefaultThreadPool(int threadCount) {
        return Executors.newFixedThreadPool(threadCount, new ThreadFactory() {

            final AtomicInteger index = new AtomicInteger(1);

            @Override public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "experiment-executor-" + index.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public ExperimentResult<R> run() {
        for (TimeSeriesProcessor processor : processors) {
            processor.init();
        }
        TimeSeriesSuite suite = readTimeSeriesSuite(suiteFile);
        progressTracker.init(suite.size(), processors.size());
        ExperimentResult.Builder<R> builder = ExperimentResult.<R>builder()
                .setTimeSeriesSuiteFileName(suiteFile.getAbsolutePath())
                .setTimeSeriesProcessorReportAggregator(aggregator);
        List<Future<?>> futures = new ArrayList<>();
        for (TimeSeriesSample sample : suite) {
            for (TimeSeriesProcessor<R> processor : processors) {
                if (!processor.support(sample)) {
                    TimeSeriesProcessorReport report = new UnsupportedSampleReport(processor.getName(), sample);
                    builder.putTimeSeriesProcessorErrorReport(processor, report);
                    continue;
                }
                futures.add(executorService.submit(() -> {
                    Long trackId = progressTracker.startSampleProcessing();
                    try {
                        R report = processor.process(sample);
                        builder.putTimeSeriesProcessorReport(processor, report);
                    } catch (Exception e) {
                        if (debugMode) {
                            throw new RuntimeException("Unexpected exception during sample processing", e);
                        }
                        TimeSeriesProcessorReport report = new UnsupportedSampleReport(processor.getName(), sample, e);
                        builder.putTimeSeriesProcessorErrorReport(processor, report);
                    } finally {
                        progressTracker.finishSampleProcessing(trackId, processor);
                    }
                }));
            }
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                if (debugMode) {
                    throw new RuntimeException("Can't process sample", e);
                }
            }
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

        void init(int totalSamplesCount, int processorsCount);

        Long startSampleProcessing();

        void finishSampleProcessing(Long trackId, TimeSeriesProcessor<?> processor);
    }

    private static final class DummyProgressTracker implements ProgressTracker {

        @Override public void init(int totalSamplesCount, int processorsCount) {
        }

        @Override public Long startSampleProcessing() {
            return null;
        }

        @Override public void finishSampleProcessing(Long trackId, TimeSeriesProcessor<?> processor) {
        }
    }

    private static final class PercentageAndTimeProgressTracker implements ProgressTracker {

        private final int threadCount;

        private final Map<TimeSeriesProcessor<?>, Duration> averageIterationDurations = new HashMap<>();

        private final Map<TimeSeriesProcessor<?>, Integer> processedSamplesCounts = new HashMap<>();

        private final Map<Long, Instant> tracks = new HashMap<>();

        private int totalSamplesCount;

        private int processorsCount;

        private int currentProgress;

        private Long previousRemainderInSeconds;

        private Instant startTimestamp;

        PercentageAndTimeProgressTracker(int threadCount) {
            this.threadCount = threadCount;
        }

        @Override public void init(int totalSamplesCount, int processorsCount) {
            this.averageIterationDurations.clear();
            this.processedSamplesCounts.clear();
            this.totalSamplesCount = totalSamplesCount;
            this.processorsCount = processorsCount;
            this.currentProgress = -1;
            this.previousRemainderInSeconds = null;
            this.startTimestamp = Instant.now();
        }

        @Override public synchronized Long startSampleProcessing() {
            long trackId = ThreadLocalRandom.current().nextLong();
            tracks.put(trackId, Instant.now());
            return trackId;
        }

        @Override public synchronized void finishSampleProcessing(Long trackId, TimeSeriesProcessor<?> processor) {
            Duration currentIterationDuration = Duration.between(tracks.remove(trackId), Instant.now());
            updateStats(processor, currentIterationDuration);
            checkProgress();
        }

        private void updateStats(TimeSeriesProcessor<?> processor, Duration currentIterationDuration) {
            Duration averageIterationDuration = averageIterationDurations.get(processor);
            if (averageIterationDuration == null) {
                averageIterationDurations.put(processor, currentIterationDuration);
                processedSamplesCounts.put(processor, 1);
                return;
            }
            int currentSamplesCount = processedSamplesCounts.get(processor) + 1;
            processedSamplesCounts.put(processor, currentSamplesCount);
            long averageIterationDurationInNanos = Math.round(
                    (double) (averageIterationDuration.toNanos() * (currentSamplesCount - 1)
                            + currentIterationDuration.toNanos())
                            / currentSamplesCount
            );
            averageIterationDurations.put(processor, Duration.ofNanos(averageIterationDurationInNanos));
        }

        private void checkProgress() {
            int newProgress = calcNewProgress();
            if (newProgress <= currentProgress) {
                return;
            }
            currentProgress = newProgress;
            if (currentProgress == 100) {
                System.out.print("100% | Total: ");
                Duration totalDuration = Duration.between(startTimestamp, Instant.now());
                if (totalDuration.toMinutes() > 0) {
                    System.out.print(totalDuration.toMinutes() + " minutes ");
                }
                System.out.println(totalDuration.getSeconds() % 60 + " seconds");
                return;
            }
            Duration remainder = calcRemainderTime();
            long seconds = remainder.getSeconds();
            if (previousRemainderInSeconds != null && previousRemainderInSeconds == seconds) {
                return;
            }
            previousRemainderInSeconds = seconds;
            if (remainder.compareTo(ChronoUnit.MINUTES.getDuration()) >= 0) {
                int minutes = (int) Math.floor(seconds / 60.0);
                System.out.println(currentProgress + "%\t| ~" + minutes + " minutes " + seconds % 60 + " seconds");
            } else {
                System.out.println(currentProgress + "%\t| ~" + (seconds < 59 ? seconds + 1 : seconds) + " seconds");
            }
        }

        private int calcNewProgress() {
            double samplesCount = processedSamplesCounts.values().stream().reduce(0, (c1, c2) -> c1 + c2);
            double ratio = samplesCount / totalSamplesCount / processorsCount;
            return (int) Math.min(Math.round(ratio * 100), 100);
        }

        private Duration calcRemainderTime() {
            Duration remainder = Duration.ZERO;
            for (Map.Entry<TimeSeriesProcessor<?>, Duration> entry : averageIterationDurations.entrySet()) {
                TimeSeriesProcessor<?> processor = entry.getKey();
                Duration averageIterationDuration = entry.getValue();
                int processedSamplesCount = processedSamplesCounts.get(processor);
                remainder = remainder.plus(averageIterationDuration.multipliedBy(totalSamplesCount - processedSamplesCount));
            }
            return remainder.dividedBy(threadCount);
        }
    }
}
