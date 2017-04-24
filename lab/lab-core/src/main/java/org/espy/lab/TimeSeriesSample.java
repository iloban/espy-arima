package org.espy.lab;

import java.io.PrintWriter;

public final class TimeSeriesSample implements Writable {

    private final TimeSeriesSampleMetadata metadata;

    private final double[] observedPart;

    private final double[] unobservedPart;

    public TimeSeriesSample(TimeSeriesSampleMetadata metadata, double[] observedPart, double[] unobservedPart) {
        this.metadata = metadata;
        this.observedPart = observedPart;
        this.unobservedPart = unobservedPart;
    }

    public TimeSeriesSampleMetadata getMetadata() {
        return metadata;
    }

    public double[] getObservedPart() {
        return observedPart;
    }

    public double[] getUnobservedPart() {
        return unobservedPart;
    }

    @Override public void write(PrintWriter writer) {
        metadata.write(writer);
        writer.println();
        writePart(writer, observedPart);
        writer.println();
        writePart(writer, unobservedPart);
    }

    private static void writePart(PrintWriter writer, double[] part) {
        int lastIndex = part.length - 1;
        for (int i = 0; i <= lastIndex; i++) {
            writer.printf("%f", part[i]);
            if (i < lastIndex) {
                writer.print(' ');
            }
        }
    }
}
