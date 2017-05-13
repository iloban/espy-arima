package org.espy.lab.arima.forecast;

import org.espy.arima.ArimaProcess;
import org.espy.arima.DefaultArimaForecaster;

import java.io.PrintWriter;
import java.util.Arrays;

public final class NaiveOneStepAheadArimaForecaster implements ArimaForecaster {

    public double[] forecast(ArimaProcess arimaProcess, double[] observedPart, int forecastLength) {
        double[] forecast = new double[forecastLength];
        for (int i = 0; i < forecastLength; i++) {
            double[] observations = Arrays.copyOf(observedPart, observedPart.length + i);
            System.arraycopy(forecast, 0, observations, observedPart.length - 1, i);
            DefaultArimaForecaster forecaster = new DefaultArimaForecaster(arimaProcess, observations);
            forecast[i] = forecaster.next();
        }
        return forecast;
    }

    @Override public void write(PrintWriter writer) {
        writer.print("espy naive, one-step ahead forecast");
    }
}
