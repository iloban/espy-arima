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
        if (observationErrors.length == 0) {
            return;
        }
        ArrayUtils.shiftToLeft(observationErrors);
        observationErrors[observationErrors.length - 1] = nextObservationError;
    }

    public double getLastObservationError() {
        return lastObservationError;
    }
}
