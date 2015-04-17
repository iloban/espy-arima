package org.espy.arima;

public interface ArimaProcessRealization extends ArimaProcess {
    double next();

    double[] next(int size);
}
