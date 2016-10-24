package org.espy.lab;

import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

public final class TimeSeriesSuite {

    private final List<TimeSeriesSample> samples;

    private TimeSeriesSuite(List<TimeSeriesSample> samples) {
        this.samples = samples;
    }

    public static TimeSeriesSuite of(List<TimeSeriesSample> cases) {
        return new TimeSeriesSuite(cases);
    }

    public void marshal(PrintWriter writer) {
        ListIterator<TimeSeriesSample> iterator = samples.listIterator();
        while (iterator.hasNext()) {
            marshal(iterator.next(), writer);
            if (iterator.hasNext()) {
                writer.print("\n\n");
            }
        }
    }

    private static void marshal(TimeSeriesSample sample, PrintWriter writer) {
        sample.getMetadata().marshal(writer);
        writer.println();
        double[] values = sample.getValues();
        int lastIndex = values.length - 1;
        for (int i = 0; i <= lastIndex; i++) {
            writer.printf("%f", values[i]);
            if (i < lastIndex) {
                writer.print(' ');
            }
        }
    }
}
