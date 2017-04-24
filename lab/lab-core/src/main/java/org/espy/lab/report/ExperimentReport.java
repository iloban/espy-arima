package org.espy.lab.report;

import org.espy.lab.processor.TimeSeriesProcessor;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

public class ExperimentReport<R extends TimeSeriesProcessorReport> implements Report {

    private final String suiteFileName;

    private final Map<TimeSeriesProcessor<R>, List<R>> reports;

    private final Map<TimeSeriesProcessor<R>, AggregatedTimeSeriesProcessorReport> aggregatedReports;

    private final Map<TimeSeriesProcessor<R>, List<TimeSeriesProcessorReport>> errorReports;

    private ExperimentReport(String suiteFileName,
                             Map<TimeSeriesProcessor<R>, List<R>> reports,
                             Map<TimeSeriesProcessor<R>, AggregatedTimeSeriesProcessorReport> aggregatedReports,
                             Map<TimeSeriesProcessor<R>, List<TimeSeriesProcessorReport>> errorReports) {
        this.suiteFileName = suiteFileName;
        this.reports = reports;
        this.aggregatedReports = aggregatedReports;
        this.errorReports = errorReports;
    }

    public static <R extends TimeSeriesProcessorReport> Builder<R> builder() {
        return new Builder<>();
    }

    @Override public void write(PrintWriter writer) {
        writer.println("Time series suite file name:");
        writer.println(suiteFileName);
        writer.println("Timestamp:");
        writer.println(LocalDateTime.now());
        writer.println();
        int i = 1;
        for (Map.Entry<TimeSeriesProcessor<R>, List<R>> entry : reports.entrySet()) {
            if (i > 1) {
                writer.println();
                writer.println();
            }
            writer.print(i++);
            writer.print(" ");
            TimeSeriesProcessor<R> processor = entry.getKey();
            processor.write(writer);
            writer.println();
            writer.println();
            aggregatedReports.get(processor).write(writer);
            writer.println();
            writer.println();
            if (errorReports.containsKey(processor)) {
                writeReports(writer, errorReports.get(processor).iterator());
                writer.println();
                writer.println();
            }
            writeReports(writer, entry.getValue().iterator());
        }
    }

    private void writeReports(PrintWriter writer, Iterator<? extends TimeSeriesProcessorReport> reportIterator) {
        while (reportIterator.hasNext()) {
            reportIterator.next().write(writer);
            if (reportIterator.hasNext()) {
                writer.println();
            }
        }
    }

    public static final class Builder<R extends TimeSeriesProcessorReport> {

        private String suiteFileName;

        private TimeSeriesProcessorReportAggregator<R> aggregator;

        private Map<TimeSeriesProcessor<R>, List<R>> reports = new LinkedHashMap<>();

        private Map<TimeSeriesProcessor<R>, List<TimeSeriesProcessorReport>> errorReports = new LinkedHashMap<>();

        public ExperimentReport<R> build() {
            Map<TimeSeriesProcessor<R>, AggregatedTimeSeriesProcessorReport> aggregatedReports = new HashMap<>();
            for (Map.Entry<TimeSeriesProcessor<R>, List<R>> entry : reports.entrySet()) {
                aggregatedReports.put(entry.getKey(), aggregator.aggregate(entry.getValue()));
            }
            return new ExperimentReport<>(suiteFileName, reports, aggregatedReports, errorReports);
        }

        public Builder<R> setTimeSeriesSuiteFileName(String fileName) {
            this.suiteFileName = fileName;
            return this;
        }

        public Builder<R> setTimeSeriesProcessorReportAggregator(TimeSeriesProcessorReportAggregator<R> aggregator) {
            this.aggregator = aggregator;
            return this;
        }

        public Builder<R> putTimeSeriesProcessorReport(TimeSeriesProcessor<R> processor, R report) {
            List<R> reportsList = reports.computeIfAbsent(processor, key -> new ArrayList<>());
            reportsList.add(report);
            return this;
        }

        public Builder<R> putTimeSeriesProcessorErrorReport(TimeSeriesProcessor<R> processor, TimeSeriesProcessorReport errorReport) {
            List<TimeSeriesProcessorReport> reportsList = errorReports.computeIfAbsent(processor, key -> new ArrayList<>());
            reportsList.add(errorReport);
            return this;
        }
    }
}
