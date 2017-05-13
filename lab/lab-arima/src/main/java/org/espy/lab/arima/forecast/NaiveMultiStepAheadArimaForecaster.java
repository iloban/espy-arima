package org.espy.lab.arima.forecast;

import org.espy.arima.ArimaProcess;
import org.espy.arima.DefaultArimaForecaster;

import java.io.PrintWriter;

public final class NaiveMultiStepAheadArimaForecaster implements ArimaForecaster {

    public double[] forecast(ArimaProcess arimaProcess, double[] observedPart, int forecastLength) {
        DefaultArimaForecaster forecaster = new DefaultArimaForecaster(arimaProcess, observedPart);
        return forecaster.next(forecastLength);
    }

    @Override public void write(PrintWriter writer) {
        writer.print("espy naive, multi-step ahead forecast");
    }
}
