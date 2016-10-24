package org.espy.lab;

public final class TimeSeriesSample {

    private final TimeSeriesSampleMetadata metadata;

    private final double[] values;

    public TimeSeriesSample(TimeSeriesSampleMetadata metadata, double[] values) {
        this.metadata = metadata;
        this.values = values;
    }

    public TimeSeriesSampleMetadata getMetadata() {
        return metadata;
    }

    public double[] getValues() {
        return values;
    }
}
