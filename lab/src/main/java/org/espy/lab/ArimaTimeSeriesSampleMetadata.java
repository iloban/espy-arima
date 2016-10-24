package org.espy.lab;

import java.io.PrintWriter;

public class ArimaTimeSeriesSampleMetadata implements TimeSeriesSampleMetadata {

    private final int integrationOrder;

    private final double[] arCoefficients;

    private final double[] maCoefficients;

    public ArimaTimeSeriesSampleMetadata(double[] arCoefficients, int integrationOrder, double[] maCoefficients) {
        this.integrationOrder = integrationOrder;
        this.arCoefficients = arCoefficients;
        this.maCoefficients = maCoefficients;
    }

    @Override public void marshal(PrintWriter writer) {
        writer.print("type=");
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
