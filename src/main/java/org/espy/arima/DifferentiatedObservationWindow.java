package org.espy.arima;

class DifferentiatedObservationWindow {
    private double[] lastObservationColumn;
    private double[] differentiatedObservations;

    public DifferentiatedObservationWindow(int integrationOrder, int arOrder) {
        lastObservationColumn = new double[integrationOrder + 1];
        differentiatedObservations = new double[arOrder];
    }

    public double[] getDifferentiatedObservations() {
        return differentiatedObservations;
    }

    public void addNextDifferentiatedObservation(double nextDifferentiatedObservation) {
        ArrayUtils.appendWithShift(differentiatedObservations, nextDifferentiatedObservation);
        lastObservationColumn[0] = nextDifferentiatedObservation;
        for (int i = 1; i < lastObservationColumn.length; i++) {
            lastObservationColumn[i] += lastObservationColumn[i - 1];
        }
    }

    public double getLastObservation() {
        return ArrayUtils.getLast(lastObservationColumn);
    }
}
