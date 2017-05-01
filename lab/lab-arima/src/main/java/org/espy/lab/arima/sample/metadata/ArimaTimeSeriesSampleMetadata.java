package org.espy.lab.arima.sample.metadata;

import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadataType;

import java.io.PrintWriter;
import java.util.Scanner;

public class ArimaTimeSeriesSampleMetadata implements TimeSeriesSampleMetadata {

    private static final String NAME = "ARIMA";

    private final double[] arCoefficients;

    private final int integrationOrder;

    private final double[] maCoefficients;

    private final double constant;

    private final double shockExpectation;

    private final double shockVariation;

    private final int observedPartLength;

    private final int unobservedPartLength;

    public ArimaTimeSeriesSampleMetadata(double[] arCoefficients,
                                         int integrationOrder,
                                         double[] maCoefficients,
                                         int observedPartLength,
                                         int unobservedPartLength) {
        this(arCoefficients, integrationOrder, maCoefficients,
                0, 0, 1,
                observedPartLength, unobservedPartLength);
    }

    public ArimaTimeSeriesSampleMetadata(double[] arCoefficients,
                                         int integrationOrder,
                                         double[] maCoefficients,
                                         double constant,
                                         double shockExpectation,
                                         double shockVariation,
                                         int observedPartLength,
                                         int unobservedPartLength) {
        this.integrationOrder = integrationOrder;
        this.arCoefficients = arCoefficients;
        this.maCoefficients = maCoefficients;
        this.constant = constant;
        this.shockExpectation = shockExpectation;
        this.shockVariation = shockVariation;
        this.observedPartLength = observedPartLength;
        this.unobservedPartLength = unobservedPartLength;
    }

    public static void register() {
        TimeSeriesSampleMetadataType.register(NAME, ArimaTimeSeriesSampleMetadata::read);
    }

    public static ArimaTimeSeriesSampleMetadata read(Scanner scanner) {
        // " | obs_len=40 | unobs_len=10 | p=0 d=2 q=2 | ma1=0.1 ma2=0.407 | mu=0.0 | E(eps)=0.0 | V(eps)=1.0"
        scanner.next();
        String lexeme = scanner.next();
        int observedPartLength = Integer.parseInt(lexeme.substring(8));
        scanner.next();
        lexeme = scanner.next();
        int unobservedPartLength = Integer.parseInt(lexeme.substring(10));
        scanner.next();
        lexeme = scanner.next();
        int p = Integer.parseInt(lexeme.substring(2));
        lexeme = scanner.next();
        int d = Integer.parseInt(lexeme.substring(2));
        lexeme = scanner.next();
        int q = Integer.parseInt(lexeme.substring(2));
        scanner.next();
        double[] arCoefficients = new double[p];
        for (int i = 0; i < p; i++) {
            lexeme = scanner.next();
            arCoefficients[i] = Double.parseDouble(lexeme.substring(4));
        }
        if (p > 0 && q > 0) {
            scanner.next();
        }
        double[] maCoefficients = new double[q];
        for (int i = 0; i < q; i++) {
            lexeme = scanner.next();
            maCoefficients[i] = Double.parseDouble(lexeme.substring(4));
        }
        scanner.next();
        lexeme = scanner.next();
        double constant = Double.parseDouble(lexeme.substring(3));
        scanner.next();
        lexeme = scanner.next();
        double shockExpectation = Double.parseDouble(lexeme.substring(7));
        scanner.next();
        lexeme = scanner.next();
        double shockVariation = Double.parseDouble(lexeme.substring(7));
        return new ArimaTimeSeriesSampleMetadata(arCoefficients, d, maCoefficients,
                constant, shockExpectation, shockVariation,
                observedPartLength, unobservedPartLength);
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

    public double getConstant() {
        return constant;
    }

    public double getShockExpectation() {
        return shockExpectation;
    }

    public double getShockVariation() {
        return shockVariation;
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
        return builder.append(" | mu=").append(constant)
                .append(" | E(eps)=").append(shockExpectation)
                .append(" | V(eps)=").append(shockVariation)
                .toString();
    }
}
