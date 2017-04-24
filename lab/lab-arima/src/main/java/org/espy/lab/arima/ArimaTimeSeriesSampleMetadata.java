package org.espy.lab.arima;

import org.espy.lab.TimeSeriesSampleMetadata;
import org.espy.lab.TimeSeriesSampleMetadataType;

import java.io.PrintWriter;
import java.util.Scanner;

public class ArimaTimeSeriesSampleMetadata implements TimeSeriesSampleMetadata {

    private static final String NAME = "ARIMA";

    private final int integrationOrder;

    private final double[] arCoefficients;

    private final double[] maCoefficients;

    private final int observedPartLength;

    private final int unobservedPartLength;

    public ArimaTimeSeriesSampleMetadata(double[] arCoefficients,
                                         int integrationOrder,
                                         double[] maCoefficients,
                                         int observedPartLength,
                                         int unobservedPartLength) {
        this.integrationOrder = integrationOrder;
        this.arCoefficients = arCoefficients;
        this.maCoefficients = maCoefficients;
        this.observedPartLength = observedPartLength;
        this.unobservedPartLength = unobservedPartLength;
    }

    public static void register() {
        TimeSeriesSampleMetadataType.register(NAME, ArimaTimeSeriesSampleMetadata::read);
    }

    public static ArimaTimeSeriesSampleMetadata read(Scanner scanner) {
        // " | obs_len=40 | unobs_len=10 | p=0 d=2 q=2 | ma1=0.1 ma2=0.407"
        scanner.next();
        String raw = scanner.next();
        int observedPartLength = Integer.parseInt(raw.substring(8));
        scanner.next();
        raw = scanner.next();
        int unobservedPartLength = Integer.parseInt(raw.substring(10));
        scanner.next();
        raw = scanner.next();
        int p = Integer.parseInt(raw.substring(2));
        raw = scanner.next();
        int d = Integer.parseInt(raw.substring(2));
        raw = scanner.next();
        int q = Integer.parseInt(raw.substring(2));
        scanner.next();
        double[] arCoefficients = new double[p];
        for (int i = 0; i < p; i++) {
            raw = scanner.next();
            arCoefficients[i] = Double.parseDouble(raw.substring(4));
        }
        if (p > 0 && q > 0) {
            scanner.next();
        }
        double[] maCoefficients = new double[q];
        for (int i = 0; i < q; i++) {
            raw = scanner.next();
            maCoefficients[i] = Double.parseDouble(raw.substring(4));
        }
        return new ArimaTimeSeriesSampleMetadata(arCoefficients, d, maCoefficients, observedPartLength, unobservedPartLength);
    }

    public int getIntegrationOrder() {
        return integrationOrder;
    }

    public double[] getArCoefficients() {
        return arCoefficients;
    }

    public double[] getMaCoefficients() {
        return maCoefficients;
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
        StringBuilder builder = new StringBuilder(NAME)
                .append(" | obs_len=")
                .append(observedPartLength)
                .append(" | unobs_len=")
                .append(unobservedPartLength)
                .append(" | p=")
                .append(arCoefficients.length)
                .append(" d=")
                .append(integrationOrder)
                .append(" q=")
                .append(maCoefficients.length)
                .append(" |");
        for (int i = 0; i < arCoefficients.length; i++) {
            builder.append(" ar").append(i + 1).append("=").append(arCoefficients[i]);
        }
        if (arCoefficients.length > 0 && maCoefficients.length > 0) {
            builder.append(" |");
        }
        for (int i = 0; i < maCoefficients.length; i++) {
            builder.append(" ma").append(i + 1).append("=").append(maCoefficients[i]);
        }
        return builder.toString();
    }
}
