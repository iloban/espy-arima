package org.espy.arima;

import java.util.Arrays;

class ForecastAccuracyRelativeMetric {
    private ForecastAccuracyRelativeMetric() {
    }

    public static double getValue(double[] observations, double[] forecast) {
        int observationLength = observations.length;
        int forecastLength = forecast.length;
        double[] deltas = new double[forecastLength];
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0, j = observationLength - forecastLength; i < forecastLength; i++, j++) {
            double observation = observations[j];
            if (min > observation) {
                min = observation;
            }
            if (max < observation) {
                max = observation;
            }
            double forecastValue = forecast[i];
            deltas[i] = Math.abs(observation - forecastValue);
        }

        for (int i = observationLength - forecastLength - 1; i >= 0; i--) {
            double observation = observations[i];
            if (min > observation) {
                min = observation;
            }
            if (max < observation) {
                max = observation;
            }
        }

        Arrays.sort(deltas);
        double denominator = (max - min) + deltas[forecastLength - 1];
        for (int i = 0; i < forecastLength; i++) {
            deltas[i] /= denominator;
        }
        double meanDelta = DoubleUtils.getMean(deltas);
        double medianDelta = deltas[forecastLength / 2];
        double maxDelta = deltas[forecastLength - 1];
        return (4 * meanDelta + 5 * medianDelta + 3 * maxDelta) / 12;
    }

    public static double combine(double[] metrics) {
        Arrays.sort(metrics);
        return combineSorted(metrics);
    }

    public static double combineSorted(double[] metrics) {
        double mean = DoubleUtils.getMean(metrics);
        double median = metrics[metrics.length / 2];
        double max = metrics[metrics.length - 1];
        return (4 * mean + 5 * median + 3 * max) / 12;
    }
}
