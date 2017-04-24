package org.espy.lab.forecast.farm;

import org.espy.arima.ForecastAccuracyRelativeMetric;
import org.espy.lab.report.TimeSeriesProcessorReportProducer;
import org.espy.lab.report.farm.FarmTimeSeriesProcessorReport;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;

import java.io.PrintWriter;

public final class FarmForecastComparator implements TimeSeriesProcessorReportProducer<FarmTimeSeriesProcessorReport> {

    @Override public FarmTimeSeriesProcessorReport produce(TimeSeriesSampleMetadata metadata,
                                                           double[] observations,
                                                           double[] forecast) {
        double farmValue = ForecastAccuracyRelativeMetric.getValue(observations, forecast);
        return FarmTimeSeriesProcessorReport.normal(metadata, farmValue);
    }

    @Override public void write(PrintWriter writer) {
        writer.print("deprecated FARM comparator");
    }
}
