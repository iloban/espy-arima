package org.espy.lab.suite;

import org.espy.lab.sample.TimeSeriesSample;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadataParser;
import org.espy.lab.util.Writable;

import java.io.PrintWriter;
import java.util.*;

public final class TimeSeriesSuite implements Iterable<TimeSeriesSample>, Writable {

    private final List<TimeSeriesSample> samples;

    public TimeSeriesSuite(List<TimeSeriesSample> samples) {
        this.samples = samples;
    }

    public static TimeSeriesSuite read(Scanner scanner) {
        List<TimeSeriesSample> samples = new ArrayList<>();
        while (scanner.hasNextLine()) {
            samples.add(readTimeSeriesSample(scanner));
        }
        return new TimeSeriesSuite(samples);
    }

    private static TimeSeriesSample readTimeSeriesSample(Scanner scanner) {
        TimeSeriesSampleMetadata metadata = TimeSeriesSampleMetadataParser.read(scanner);
        double[] observedPart = new double[metadata.getObservedPartLength()];
        readTimeSeriesSamplePart(scanner, observedPart);
        double[] unobservedPart = new double[metadata.getUnobservedPartLength()];
        readTimeSeriesSamplePart(scanner, unobservedPart);
        return new TimeSeriesSample(metadata, observedPart, unobservedPart);
    }

    private static void readTimeSeriesSamplePart(Scanner scanner, double[] part) {
        for (int i = 0; i < part.length; i++) {
            part[i] = scanner.nextDouble();
        }
    }

    @Override public Iterator<TimeSeriesSample> iterator() {
        return samples.iterator();
    }

    @Override public void write(PrintWriter writer) {
        ListIterator<TimeSeriesSample> iterator = samples.listIterator();
        while (iterator.hasNext()) {
            iterator.next().write(writer);
            if (iterator.hasNext()) {
                writer.print("\n\n");
            }
        }
    }

    public int size() {
        return samples.size();
    }
}
