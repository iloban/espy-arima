package org.espy.lab;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

public class ExperimentReport implements Report {

    private final String suiteFileName;

    private final Map<TimeSeriesProcessor, List<TimeSeriesProcessorReport>> reports;

    private final Map<TimeSeriesProcessor, AggregatedTimeSeriesProcessorReport> aggregatedReports;

    private ExperimentReport(String suiteFileName,
                             Map<TimeSeriesProcessor, List<TimeSeriesProcessorReport>> reports,
                             Map<TimeSeriesProcessor, AggregatedTimeSeriesProcessorReport> aggregatedReports) {
        this.suiteFileName = suiteFileName;
        this.reports = reports;
        this.aggregatedReports = aggregatedReports;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override public void write(PrintWriter writer) {
        writer.println("Time series suite file name: " + suiteFileName);
        writer.println("Timestamp: " + LocalDateTime.now());
        writer.println();
        int i = 1;
        for (Map.Entry<TimeSeriesProcessor, List<TimeSeriesProcessorReport>> entry : reports.entrySet()) {
            TimeSeriesProcessor processor = entry.getKey();
            Iterator<TimeSeriesProcessorReport> reportIterator = entry.getValue().iterator();
            AggregatedTimeSeriesProcessorReport aggregatedReport = aggregatedReports.get(processor);
            if (i > 1) {
                writer.println();
                writer.println();
            }
            writer.print(i++);
            writer.print(". ");
            processor.write(writer);
            writer.println();
            writer.println();
            aggregatedReport.write(writer);
            writer.println();
            writer.println();
            while (reportIterator.hasNext()) {
                reportIterator.next().write(writer);
                if (reportIterator.hasNext()) {
                    writer.println();
                }
            }
        }
    }

    public static final class Builder {

        private String suiteFileName;

        private TimeSeriesProcessorReportAggregator aggregator;

        private Map<TimeSeriesProcessor, List<TimeSeriesProcessorReport>> reports = new LinkedHashMap<>();

        public ExperimentReport build() {
            Map<TimeSeriesProcessor, AggregatedTimeSeriesProcessorReport> aggregatedReports = new HashMap<>();
            for (Map.Entry<TimeSeriesProcessor, List<TimeSeriesProcessorReport>> entry : reports.entrySet()) {
                aggregatedReports.put(entry.getKey(), aggregator.aggregate(entry.getValue()));
            }
            return new ExperimentReport(suiteFileName, reports, aggregatedReports);
        }

        public Builder setTimeSeriesSuiteFileName(String fileName) {
            this.suiteFileName = fileName;
            return this;
        }

        public Builder setTimeSeriesProcessorReportAggregator(TimeSeriesProcessorReportAggregator aggregator) {
            this.aggregator = aggregator;
            return this;
        }

        public Builder putTimeSeriesProcessorReport(TimeSeriesProcessor processor, TimeSeriesProcessorReport report) {
            List<TimeSeriesProcessorReport> reportsList = reports.computeIfAbsent(processor, key -> new ArrayList<>());
            reportsList.add(report);
            return this;
        }
    }
}
