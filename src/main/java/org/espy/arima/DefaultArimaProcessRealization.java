package org.espy.arima;

public class DefaultArimaProcessRealization implements ArimaProcessRealization {
    private ArimaProcess arimaProcess;
    private IntegratedObservationWindow integratedObservationWindow;
    private ObservationErrorWindow observationErrorWindow;
    private ArmaFormula armaFormula;

    public DefaultArimaProcessRealization(ArimaProcess arimaProcess) {
        this.arimaProcess = arimaProcess;
        this.integratedObservationWindow =
                new IntegratedObservationWindow(arimaProcess.getIntegratedOrder(), arimaProcess.getArOrder());
        this.observationErrorWindow = new ObservationErrorWindow(arimaProcess.getMaOrder());
        armaFormula = new ArmaFormula();
        armaFormula.setArCoefficients(arimaProcess.getArCoefficients());
        armaFormula.setMaCoefficients(arimaProcess.getMaCoefficients());
        armaFormula.setStandardDeviation(Math.sqrt(arimaProcess.getVariance()));
        armaFormula.setConstant(arimaProcess.getConstant());
    }

    @Override
    public double[] getArCoefficients() {
        return arimaProcess.getArCoefficients();
    }

    @Override
    public double next() {
        double[] arArguments = integratedObservationWindow.getIntegratedObservations();
        double[] maArguments = observationErrorWindow.getObservationErrors();

        ArmaFormula.Result result = armaFormula.evaluate(arArguments, maArguments);

        integratedObservationWindow.addNextIntegratedObservation(result.observation);
        observationErrorWindow.addNextObservationError(result.observationError);

        return integratedObservationWindow.getLastObservation();
    }

    @Override
    public double[] getMaCoefficients() {
        return arimaProcess.getMaCoefficients();
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
    public double getVariance() {
        return arimaProcess.getVariance();
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
    public int getIntegratedOrder() {
        return arimaProcess.getIntegratedOrder();
    }

    @Override
    public int getMaOrder() {
        return arimaProcess.getMaOrder();
    }

    @Override
    public String toString() {
        return "ArimaProcessRealization{" +
                "arimaProcess=" + arimaProcess +
                '}';
    }
}
