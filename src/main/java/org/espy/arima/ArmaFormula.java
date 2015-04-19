package org.espy.arima;

import java.util.Random;

class ArmaFormula {
    private Random random = new Random();
    private double[] arCoefficients;
    private double[] maCoefficients;
    private double expectation;
    private double standardDeviation;
    private double constant;

    public Result evaluate(double[] arArguments, double[] maArguments) {
        Result result = new Result();
        result.observationError = getObservationError();
        result.observation = constant + result.observationError + getVectorConvolution(arCoefficients, arArguments)
                + getVectorConvolution(maCoefficients, maArguments);
        return result;
    }

    private double getObservationError() {
        // TODO (feature): add facility to configure error computation.
        return expectation + random.nextGaussian() * standardDeviation;
    }

    private double getVectorConvolution(double[] vector1, double[] vector2) {
        double result = 0;
        for (int i = 0; i < vector1.length; i++) {
            result += vector1[i] * vector2[vector2.length - i - 1];
        }
        return result;
    }

    public void setArCoefficients(double[] arCoefficients) {
        this.arCoefficients = arCoefficients;
    }

    public void setMaCoefficients(double[] maCoefficients) {
        this.maCoefficients = maCoefficients;
    }

    public void setExpectation(double expectation) {
        this.expectation = expectation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }

    public static class Result {
        public double observation;
        public double observationError;
    }
}
