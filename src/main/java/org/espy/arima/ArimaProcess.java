package org.espy.arima;

public interface ArimaProcess extends ArimaModel {
    double[] getArCoefficients();

    double[] getMaCoefficients();

    double getExpectation();

    double getVariance();

    double getConstant();
}
