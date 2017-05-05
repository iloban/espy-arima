package org.espy.lab.report.farm;

import org.espy.arima.ForecastAccuracyRelativeMetric;
import org.espy.lab.report.AggregatedTimeSeriesProcessorReport;
import org.espy.lab.report.TimeSeriesProcessorReportAggregator;

import java.io.PrintWriter;
import java.util.List;

public final class FarmTimeSeriesProcessorReportAggregator
        implements TimeSeriesProcessorReportAggregator<FarmTimeSeriesProcessorReport> {

    @Override public AggregatedTimeSeriesProcessorReport aggregate(List<FarmTimeSeriesProcessorReport> reports) {
        double[] values = reports.stream()
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
