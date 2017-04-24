package org.espy.lab.arima;

import org.espy.arima.ArimaProcess;
import org.espy.arima.DefaultArimaForecaster;
import org.espy.lab.Writable;

import java.io.PrintWriter;
import java.util.Arrays;

public final class ArimaForecaster implements Writable {

    public double[] forecast(ArimaProcess arimaProcess, double[] observedPart, int forecastLength) {
        double[] forecast = new double[forecastLength];
        for (int i = 0; i < forecastLength; i++) {
            double[] observations = Arrays.copyOf(observedPart, observedPart.length + i);
            for (int j = 0; j < i; j++) {
                observations[observedPart.length + j - 1] = forecast[j];
            }
            DefaultArimaForecaster forecaster = new DefaultArimaForecaster(arimaProcess, observations);
            forecast[i] = forecaster.next();
        }
        return forecast;
    }

    @Override public void write(PrintWriter writer) {
        writer.print("espy default forecaster, one by one mode");
    }
}
