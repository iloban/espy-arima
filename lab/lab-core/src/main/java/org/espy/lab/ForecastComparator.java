package org.espy.lab;

import org.espy.arima.ForecastAccuracyRelativeMetric;

import java.io.PrintWriter;

public final class ForecastComparator implements Writable {

    public TimeSeriesProcessorReport compare(TimeSeriesSampleMetadata metadata,
                                             double[] observations,
                                             double[] forecast) {
        double farm = ForecastAccuracyRelativeMetric.getValue(observations, forecast);
        return new FarmTimeSeriesProcessorReport(metadata, farm);
    }

    @Override public void write(PrintWriter writer) {
        writer.print("deprecated FARM comparator");
    }
}
