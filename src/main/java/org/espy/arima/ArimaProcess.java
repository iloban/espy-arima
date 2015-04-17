package org.espy.arima;

public interface ArimaProcess extends ArimaModel {
    double[] getArCoefficients();

    double[] getMaCoefficients();

    double getVariance();

    double getConstant();
}
