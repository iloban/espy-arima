package org.espy.arima;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultArimaForecaster implements ArimaForecaster {
    private DifferentiatedObservationWindow differentiatedObservationWindow;
    private ObservationErrorWindow observationErrorWindow;
    private ArmaFormula armaFormula;

    public DefaultArimaForecaster(ArimaProcess arimaProcess, double[] observations) {
        differentiatedObservationWindow =
                new DifferentiatedObservationWindow(arimaProcess.getIntegrationOrder(), observations);

        observationErrorWindow = new ObservationErrorWindow(arimaProcess.getMaOrder(), arimaProcess.getShockExpectation());

        armaFormula = new ArmaFormula(ThreadLocalRandom.current());
        armaFormula.setArCoefficients(arimaProcess.getArCoefficients());
        armaFormula.setMaCoefficients(arimaProcess.getMaCoefficients());
        armaFormula.setExpectation(arimaProcess.getShockExpectation());
        armaFormula.setStandardDeviation(0);
        armaFormula.setConstant(arimaProcess.getConstant());

        double[] differentiatedObservations = differentiatedObservationWindow.getDifferentiatedObservations();
        int arOrder = arimaProcess.getArOrder();
        for (int i = arOrder; i < differentiatedObservations.length; i++) {
            double[] arArguments = Arrays.copyOfRange(differentiatedObservations, i - arOrder, i);
            double[] maArguments = observationErrorWindow.getObservationErrors();
            double estimatedObservation = armaFormula.evaluate(arArguments, maArguments).observation;
            double estimatedError = differentiatedObservations[i] - estimatedObservation;
            observationErrorWindow.pushObservationError(estimatedError);
        }

        differentiatedObservationWindow.setWindowSize(arOrder);
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
}
