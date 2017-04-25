package org.espy.lab.arima.forecast;

import org.espy.arima.ArimaProcess;
import org.espy.lab.util.Writable;

public interface ArimaForecaster extends Writable {

    double[] forecast(ArimaProcess arimaProcess, double[] observedPart, int forecastLength);
}
