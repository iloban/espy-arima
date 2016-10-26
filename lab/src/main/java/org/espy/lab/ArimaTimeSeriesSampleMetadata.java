package org.espy.lab;

import java.io.PrintWriter;
import java.util.Scanner;

public class ArimaTimeSeriesSampleMetadata implements TimeSeriesSampleMetadata {

    private final int integrationOrder;

    private final double[] arCoefficients;

    private final double[] maCoefficients;

    public ArimaTimeSeriesSampleMetadata(double[] arCoefficients, int integrationOrder, double[] maCoefficients) {
        this.integrationOrder = integrationOrder;
        this.arCoefficients = arCoefficients;
        this.maCoefficients = maCoefficients;
    }

    public static ArimaTimeSeriesSampleMetadata unmarshal(Scanner scanner) {
        // " | p=0 d=2 q=2 | ma1=0.1 ma2=0.407"
        scanner.next();
        String raw = scanner.next();
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
        return new ArimaTimeSeriesSampleMetadata(arCoefficients, d, maCoefficients);
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

    @Override public void marshal(PrintWriter writer) {
        writer.print(getType());
        writer.print(" | p=");
        writer.print(arCoefficients.length);
        writer.print(" d=");
        writer.print(integrationOrder);
        writer.print(" q=");
        writer.print(maCoefficients.length);
        writer.print(" |");
        for (int i = 0; i < arCoefficients.length; i++) {
            writer.print(" ar" + (i + 1) + "=" + arCoefficients[i]);
        }
        if (arCoefficients.length > 0 && maCoefficients.length > 0) {
            writer.print(" |");
        }
        for (int i = 0; i < maCoefficients.length; i++) {
            writer.print(" ma" + (i + 1) + "=" + maCoefficients[i]);
        }
    }

    @Override public TimeSeriesSampleType getType() {
        return TimeSeriesSampleType.ARIMA;
    }
}
