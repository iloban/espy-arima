package org.espy.lab.sample.metadata;

import java.io.PrintWriter;
import java.util.Scanner;

public class UnknownTimeSeriesSampleMetadata implements TimeSeriesSampleMetadata {

    public static final String NAME = "UNKNOWN";

    private final int observedPartLength;

    private final int unobservedPartLength;

    public UnknownTimeSeriesSampleMetadata(int observedPartLength, int unobservedPartLength) {
        this.observedPartLength = observedPartLength;
        this.unobservedPartLength = unobservedPartLength;
    }

    public static UnknownTimeSeriesSampleMetadata read(Scanner scanner) {
        // " | obs_len=40 | unobs_len=10"
        scanner.next();
        String lexeme = scanner.next();
        int observedPartLength = Integer.parseInt(lexeme.substring(8));
        scanner.next();
        lexeme = scanner.next();
        int unobservedPartLength = Integer.parseInt(lexeme.substring(10));
        return new UnknownTimeSeriesSampleMetadata(observedPartLength, unobservedPartLength);
    }

    @Override public void write(PrintWriter writer) {
        writer.print(this);
    }

    @Override public int getObservedPartLength() {
        return observedPartLength;
    }

    @Override public int getUnobservedPartLength() {
        return unobservedPartLength;
    }

    @Override public String toString() {
        return NAME +
                " | obs_len=" +
                observedPartLength +
                " | unobs_len=" +
                unobservedPartLength;
    }
}
