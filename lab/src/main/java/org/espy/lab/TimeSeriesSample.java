package org.espy.lab;

import java.util.List;

public final class TimeSeriesSample {

    private final TimeSeriesSampleMetadata metadata;

    private final double[] values;

    public TimeSeriesSample(TimeSeriesSampleMetadata metadata, List<Double> values) {
        this(metadata, toArray(values));
    }

    public TimeSeriesSample(TimeSeriesSampleMetadata metadata, double[] values) {
        this.metadata = metadata;
        this.values = values;
    }

    private static double[] toArray(List<Double> values) {
        double[] result = new double[values.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    public TimeSeriesSampleMetadata getMetadata() {
        return metadata;
    }

    public double[] getValues() {
        return values;
    }
}
