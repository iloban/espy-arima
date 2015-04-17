package org.espy.arima;

import java.util.Arrays;

public class DefaultArimaProcess implements ArimaProcess {
    private double[] arCoefficients;
    private double[] maCoefficients;
    private int integratedOrder;
    private double variance = 1;
    private double constant;

    @Override
    public int getArOrder() {
        return arCoefficients.length;
    }

    @Override
    public int getIntegratedOrder() {
        return integratedOrder;
    }

    @Override
    public int getMaOrder() {
        return maCoefficients.length;
    }

    public void setIntegratedOrder(int integratedOrder) {
        this.integratedOrder = integratedOrder;
    }

    @Override
    public double[] getArCoefficients() {
        return arCoefficients;
    }

    public void setArCoefficients(double[] arCoefficients) {
        this.arCoefficients = arCoefficients;
    }

    @Override
    public double[] getMaCoefficients() {
        return maCoefficients;
    }

    public void setMaCoefficients(double[] maCoefficients) {
        this.maCoefficients = maCoefficients;
    }

    @Override
    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    @Override
    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return "ArimaProcess{" +
                "arCoefficients=" + Arrays.toString(arCoefficients) +
                ", maCoefficients=" + Arrays.toString(maCoefficients) +
                ", integratedOrder=" + integratedOrder +
                ", variance=" + variance +
                ", constant=" + constant +
                '}';
    }
}
