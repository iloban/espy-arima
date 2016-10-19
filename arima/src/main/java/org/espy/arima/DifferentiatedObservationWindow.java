package org.espy.arima;

class DifferentiatedObservationWindow {
    private double[] lastObservationColumn;
    private double[] differentiatedObservations;

    public DifferentiatedObservationWindow(int integrationOrder, int arOrder) {
        lastObservationColumn = new double[integrationOrder + 1];
        differentiatedObservations = new double[arOrder];
    }

    public DifferentiatedObservationWindow(int integrationOrder, double[] observations) {
        lastObservationColumn = new double[integrationOrder + 1];
        lastObservationColumn[integrationOrder] = DoubleUtils.getLast(observations);
        differentiatedObservations = observations;
        for (int i = integrationOrder - 1; i >= 0; i--) {
            differentiatedObservations = DoubleUtils.differentiate(observations);
            lastObservationColumn[i] = DoubleUtils.getLast(differentiatedObservations);
        }
    }

    public double[] getDifferentiatedObservations() {
        return differentiatedObservations;
    }

    public double pushDifferentiatedObservation(double nextDifferentiatedObservation) {
        DoubleUtils.appendWithShift(differentiatedObservations, nextDifferentiatedObservation);
        lastObservationColumn[0] = nextDifferentiatedObservation;
        for (int i = 1; i < lastObservationColumn.length; i++) {
            lastObservationColumn[i] += lastObservationColumn[i - 1];
        }
        return DoubleUtils.getLast(lastObservationColumn);
    }

    public void setWindowSize(int windowSize) {
        differentiatedObservations = DoubleUtils.copyEnd(differentiatedObservations, windowSize);
    }
}
