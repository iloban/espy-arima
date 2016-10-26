package org.espy.lab;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public final class TimeSeriesSuite {

    private final List<TimeSeriesSample> samples;

    public TimeSeriesSuite(List<TimeSeriesSample> samples) {
        this.samples = samples;
    }

    public static TimeSeriesSuite unmarshal(Scanner scanner) {
        List<TimeSeriesSample> samples = new ArrayList<>();
        while (scanner.hasNextLine()) {
            TimeSeriesSampleMetadata metadata = TimeSeriesSampleMetadataParser.unmarshal(scanner);
            List<Double> values = new ArrayList<>();
            while (scanner.hasNextDouble()) {
                values.add(scanner.nextDouble());
            }
            samples.add(new TimeSeriesSample(metadata, values));
        }
        return new TimeSeriesSuite(samples);
    }

    public List<TimeSeriesSample> getSamples() {
        return samples;
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
