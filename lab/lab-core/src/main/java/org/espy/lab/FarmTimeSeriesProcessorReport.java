package org.espy.lab;

import java.io.PrintWriter;

public final class FarmTimeSeriesProcessorReport implements TimeSeriesProcessorReport {

    private final TimeSeriesSampleMetadata metadata;

    private final double value;

    public FarmTimeSeriesProcessorReport(TimeSeriesSampleMetadata metadata, double value) {
        this.metadata = metadata;
        this.value = value;
    }

    @Override public void write(PrintWriter writer) {
        writer.printf("FARM: %f%% | ", value * 100);
        metadata.write(writer);
    }

    public TimeSeriesSampleMetadata getMetadata() {
        return metadata;
    }

    public double getValue() {
        return value;
    }
}
