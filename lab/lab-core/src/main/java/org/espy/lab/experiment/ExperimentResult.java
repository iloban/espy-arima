package org.espy.lab.experiment;

import org.espy.lab.processor.TimeSeriesProcessor;
import org.espy.lab.report.AggregatedTimeSeriesProcessorReport;
import org.espy.lab.report.Report;
import org.espy.lab.report.TimeSeriesProcessorReport;
import org.espy.lab.report.TimeSeriesProcessorReportAggregator;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

public class ExperimentResult<R extends TimeSeriesProcessorReport> {

    private final String suiteFileName;

    private final Map<TimeSeriesProcessor<R>, List<R>> reports;

    private final Map<TimeSeriesProcessor<R>, AggregatedTimeSeriesProcessorReport> aggregatedReports;

    private final Map<TimeSeriesProcessor<R>, List<TimeSeriesProcessorReport>> errorReports;

    private ExperimentResult(String suiteFileName,
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

    public Report getFullReport() {
        return new ExperimentReport<>(this, true);
    }

    public Report getShortReport() {
        return new ExperimentReport<>(this, false);
    }

    public static final class ExperimentReport<R extends TimeSeriesProcessorReport> implements Report {

        private final ExperimentResult<R> result;

        private final boolean full;

        public ExperimentReport(ExperimentResult<R> result, boolean full) {
            this.result = result;
            this.full = full;
        }

        @Override public void write(PrintWriter writer) {
            writer.println("Time series suite file name:");
            writer.println(result.suiteFileName);
            writer.println("Timestamp:");
            writer.println(LocalDateTime.now());
            writer.println();
            int i = 1;
            int size = result.reports.size();
            for (Map.Entry<TimeSeriesProcessor<R>, List<R>> entry : result.reports.entrySet()) {
                writer.print(i++);
                writer.print(". ");
                TimeSeriesProcessor<R> processor = entry.getKey();
                processor.write(writer);
                writer.println();
                writer.println();
                result.aggregatedReports.get(processor).write(writer);
                if (result.errorReports.containsKey(processor)) {
                    writer.println();
                    writer.println();
                    writer.println("Errors:");
                    writeReports(writer, result.errorReports.get(processor).iterator());
                    if (full || i <= size) {
                        writer.println();
                        writer.println();
                    }
                } else if (full || i <= size) {
                    writer.println();
                    writer.println();
                }
                if (full) {
                    writeReports(writer, entry.getValue().iterator());
                    if (i <= size) {
                        writer.println();
                        writer.println();
                    }
                }
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
    }

    public static final class Builder<R extends TimeSeriesProcessorReport> {

        private String suiteFileName;

        private TimeSeriesProcessorReportAggregator<R> aggregator;

        private Map<TimeSeriesProcessor<R>, List<R>> reports = new LinkedHashMap<>();

        private Map<TimeSeriesProcessor<R>, List<TimeSeriesProcessorReport>> errorReports = new LinkedHashMap<>();

        public ExperimentResult<R> build() {
            Map<TimeSeriesProcessor<R>, AggregatedTimeSeriesProcessorReport> aggregatedReports = new HashMap<>();
            for (Map.Entry<TimeSeriesProcessor<R>, List<R>> entry : reports.entrySet()) {
                aggregatedReports.put(entry.getKey(), aggregator.aggregate(entry.getValue()));
            }
            return new ExperimentResult<>(suiteFileName, reports, aggregatedReports, errorReports);
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
