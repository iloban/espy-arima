package org.espy.arima;

class ObservationErrorWindow {
    private double[] observationErrors;
    private double lastObservationError;

    public ObservationErrorWindow(int maOrder) {
        this.observationErrors = new double[maOrder];
    }

    public double[] getObservationErrors() {
        return observationErrors;
    }

    public void addNextObservationError(double nextObservationError) {
        lastObservationError = nextObservationError;
        ArrayUtils.appendWithShift(observationErrors, nextObservationError);
    }

    public double getLastObservationError() {
        return lastObservationError;
    }
}
