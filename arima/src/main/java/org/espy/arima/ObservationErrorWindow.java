package org.espy.arima;

import java.util.Arrays;

class ObservationErrorWindow {
    private double[] observationErrors;

    public ObservationErrorWindow(int maOrder, double defaultError) {
        this(maOrder);
        Arrays.fill(observationErrors, defaultError);
    }

    public ObservationErrorWindow(int maOrder) {
        this.observationErrors = new double[maOrder];
    }

    public double[] getObservationErrors() {
        return observationErrors;
    }

    public void pushObservationError(double nextObservationError) {
        DoubleUtils.appendWithShift(observationErrors, nextObservationError);
    }
}
