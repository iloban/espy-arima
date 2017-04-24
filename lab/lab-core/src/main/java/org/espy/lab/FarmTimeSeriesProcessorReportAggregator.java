package org.espy.lab;

import org.espy.arima.ForecastAccuracyRelativeMetric;

import java.io.PrintWriter;
import java.util.List;

public final class FarmTimeSeriesProcessorReportAggregator implements TimeSeriesProcessorReportAggregator {

    @Override public AggregatedTimeSeriesProcessorReport aggregate(List<TimeSeriesProcessorReport> reports) {
        double[] values = reports.stream()
                .map(report -> (FarmTimeSeriesProcessorReport) report)
                .mapToDouble(FarmTimeSeriesProcessorReport::getValue)
                .toArray();
        double value = ForecastAccuracyRelativeMetric.combine(values);
        return new AggregatedReport(value);
    }

    public static final class AggregatedReport implements AggregatedTimeSeriesProcessorReport {

        private final double value;

        public AggregatedReport(double value) {
            this.value = value;
        }

        @Override public void write(PrintWriter writer) {
            writer.printf("Total FARM: %f%%", value * 100);
        }
    }
}
