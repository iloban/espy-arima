package org.espy.arima;

class IntegratedObservationWindow {
    private double[][] allIntegratedObservations;
    private boolean zeroArOrder;

    public IntegratedObservationWindow(int integratedOrder, int arOrder) {
        if (arOrder < 1) {
            arOrder = 1;
            zeroArOrder = true;
        }
        allIntegratedObservations = new double[integratedOrder + 1][];
        allIntegratedObservations[0] = new double[integratedOrder + arOrder];
        for (int i = 1; i <= integratedOrder; i++) {
            allIntegratedObservations[i] = integrateObservations(allIntegratedObservations[i - 1]);
        }
    }

    private static double[] integrateObservations(double[] observations) {
        double[] integratedObservations = new double[observations.length - 1];
        for (int i = 0; i < integratedObservations.length; i++) {
            integratedObservations[i] = observations[i + 1] - observations[i];
        }
        return integratedObservations;
    }

    public double[] getIntegratedObservations() {
        if (zeroArOrder) {
            return new double[0];
        }
        int integratedOrder = allIntegratedObservations.length - 1;
        return allIntegratedObservations[integratedOrder];
    }

    public void addNextIntegratedObservation(double nextIntegratedObservation) {
        for (double[] integratedObservations : allIntegratedObservations) {
            ArrayUtils.shiftToLeft(integratedObservations);
        }
        int integratedOrder = allIntegratedObservations.length - 1;
        double[] io = allIntegratedObservations[integratedOrder];
        double[] prevIo;
        int lastIndex = io.length - 1;
        io[lastIndex] = nextIntegratedObservation;
        for (int i = integratedOrder - 1; i >= 0; i--) {
            io = allIntegratedObservations[i];
            prevIo = allIntegratedObservations[i + 1];
            io[++lastIndex] = prevIo[prevIo.length - 1] + io[lastIndex - 1];
        }
    }

    public double getLastObservation() {
        double[] io = allIntegratedObservations[0];
        return io[io.length - 1];
    }
}
