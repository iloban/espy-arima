package org.espy.lab.forecast.farm;

import org.espy.arima.ForecastAccuracyRelativeMetric;
import org.espy.lab.report.TimeSeriesProcessorReportProducer;
import org.espy.lab.report.farm.FarmTimeSeriesProcessorReport;
import org.espy.lab.sample.TimeSeriesSample;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;

import java.io.PrintWriter;

public final class FarmForecastComparator implements TimeSeriesProcessorReportProducer<FarmTimeSeriesProcessorReport> {

    @Override public FarmTimeSeriesProcessorReport produce(TimeSeriesSampleMetadata metadata,
                                                           TimeSeriesSample sample,
                                                           double[] forecast) {
        double[] observations = new double[metadata.getObservedPartLength() + metadata.getUnobservedPartLength()];
        System.arraycopy(sample.getObservedPart(), 0, observations, 0, metadata.getObservedPartLength());
        System.arraycopy(sample.getUnobservedPart(), 0, observations, metadata.getObservedPartLength(), metadata.getUnobservedPartLength());
        double farmValue = ForecastAccuracyRelativeMetric.getValue(observations, forecast);
        return new FarmTimeSeriesProcessorReport(metadata, farmValue);
    }

    @Override public void write(PrintWriter writer) {
        writer.print("deprecated FARM comparator");
    }
}
