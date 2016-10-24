package org.espy.arima;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultArimaProcessRealization implements ArimaProcessRealization {

    private ArimaProcess arimaProcess;
    private DifferentiatedObservationWindow differentiatedObservationWindow;
    private ObservationErrorWindow observationErrorWindow;
    private ArmaFormula armaFormula;

    public DefaultArimaProcessRealization(ArimaProcess arimaProcess) {
        this(arimaProcess, ThreadLocalRandom.current());
    }

    public DefaultArimaProcessRealization(ArimaProcess arimaProcess, Random random) {
        this.arimaProcess = arimaProcess;
        this.differentiatedObservationWindow =
                new DifferentiatedObservationWindow(arimaProcess.getIntegrationOrder(), arimaProcess.getArOrder());
        this.observationErrorWindow = new ObservationErrorWindow(arimaProcess.getMaOrder());
        armaFormula = new ArmaFormula(random);
        armaFormula.setArCoefficients(arimaProcess.getArCoefficients());
        armaFormula.setMaCoefficients(arimaProcess.getMaCoefficients());
        armaFormula.setExpectation(arimaProcess.getExpectation());
        armaFormula.setStandardDeviation(Math.sqrt(arimaProcess.getVariation()));
        armaFormula.setConstant(arimaProcess.getConstant());
    }

    @Override
    public double[] getArCoefficients() {
        return arimaProcess.getArCoefficients();
    }

    @Override
    public double[] getMaCoefficients() {
        return arimaProcess.getMaCoefficients();
    }

    @Override
    public double getExpectation() {
        return arimaProcess.getExpectation();
    }

    @Override
    public double getVariation() {
        return arimaProcess.getVariation();
    }

    @Override
    public double getConstant() {
        return arimaProcess.getConstant();
    }

    @Override
    public int getArOrder() {
        return arimaProcess.getArOrder();
    }

    @Override
    public int getIntegrationOrder() {
        return arimaProcess.getIntegrationOrder();
    }

    @Override
    public int getMaOrder() {
        return arimaProcess.getMaOrder();
    }

    @Override
    public double[] next(int size) {
        double[] result = new double[size];
        for (int i = 0; i < size; i++) {
            result[i] = next();
        }
        return result;
    }

    @Override
    public double next() {
        double[] arArguments = differentiatedObservationWindow.getDifferentiatedObservations();
        double[] maArguments = observationErrorWindow.getObservationErrors();
        ArmaFormula.Result result = armaFormula.evaluate(arArguments, maArguments);
        observationErrorWindow.pushObservationError(result.observationError);
        return differentiatedObservationWindow.pushDifferentiatedObservation(result.observation);
    }

    @Override
    public String toString() {
        return "ArimaProcessRealization{" +
                "arimaProcess=" + arimaProcess +
                '}';
    }
}
