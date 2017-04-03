package org.espy.lab;

import java.io.PrintWriter;

public final class ProcessorReport {

    private final TimeSeriesSampleMetadata metadata;

    private final double farm;

    public ProcessorReport(TimeSeriesSampleMetadata metadata) {
        this(metadata, -1);
    }

    public ProcessorReport(TimeSeriesSampleMetadata metadata, double farm) {
        this.metadata = metadata;
        this.farm = farm;
    }

    public void marshal(PrintWriter writer) {
        metadata.marshal(writer);
        writer.println();
        writer.printf("FARM: %f%%", farm * 100);
    }

    public TimeSeriesSampleMetadata getMetadata() {
        return metadata;
    }

    public double getFarm() {
        return farm;
    }
}
