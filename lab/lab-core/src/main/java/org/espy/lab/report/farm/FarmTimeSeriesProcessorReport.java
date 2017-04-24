package org.espy.lab.report.farm;

import org.espy.lab.report.AbstractTimeSeriesProcessorReport;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;

import java.io.PrintWriter;

public final class FarmTimeSeriesProcessorReport extends AbstractTimeSeriesProcessorReport {

    private final TimeSeriesSampleMetadata metadata;

    private final double value;

    private final String processorName;

    private FarmTimeSeriesProcessorReport(TimeSeriesSampleMetadata metadata, double value) {
        super(false);
        this.metadata = metadata;
        this.value = value;
        this.processorName = "<UNKNOWN>";
    }

    private FarmTimeSeriesProcessorReport(TimeSeriesSampleMetadata metadata, String processorName) {
        super(true);
        this.metadata = metadata;
        this.value = -1;
        this.processorName = processorName;
    }

    public static FarmTimeSeriesProcessorReport normal(TimeSeriesSampleMetadata metadata, double value) {
        return new FarmTimeSeriesProcessorReport(metadata, value);
    }

    public static FarmTimeSeriesProcessorReport error(TimeSeriesSampleMetadata metadata, String processorName) {
        return new FarmTimeSeriesProcessorReport(metadata, processorName);
    }

    @Override protected void writeNormalReport(PrintWriter writer) {
        writer.printf("FARM: %f%% | ", value * 100);
        metadata.write(writer);
    }

    @Override protected void writeErrorReport(PrintWriter writer) {

    }

    public TimeSeriesSampleMetadata getMetadata() {
        return metadata;
    }

    public double getValue() {
        return value;
    }
}
