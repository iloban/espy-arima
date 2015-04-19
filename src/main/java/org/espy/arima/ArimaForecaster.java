package org.espy.arima;

public interface ArimaForecaster {
    double next();

    double[] next(int size);
}
