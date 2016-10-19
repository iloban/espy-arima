package org.espy.arima;

class StationarityTest {
    private static final double STATIONARITY_THRESHOLD = 0.10815;

    public static Result test(double[] observations, Result previousResult) {
        // TODO (improvement): also check variation stationarity.
        if (observations.length < 2) {
            return new Result(true, 0);
        }

        double min = observations[0];
        double max = observations[0];
        double total = observations[0];
        int medianIndex = observations.length / 2;
        for (int i = 1; i < medianIndex; i++) {
            double observation = observations[i];
            total += observation;
            if (min > observation) {
                min = observation;
            } else if (max < observation) {
                max = observation;
            }
        }

        double sum1 = total;
        for (int i = medianIndex; i < observations.length; i++) {
            double observation = observations[i];
            total += observation;
            if (min > observation) {
                min = observation;
            } else if (max < observation) {
                max = observation;
            }
        }

        if (DoubleUtils.isApproximateEqual(min, max)) {
            return new Result(true, min);
        }

        double sum2 = total - sum1;
        double mean = total / observations.length;
        double mean1 = sum1 / medianIndex;
        double mean2 = sum2 / (observations.length - medianIndex);
        double c1 = Math.abs(mean - mean1) + Math.abs(mean - mean2);
        double c2 = max - min;

        double stationarityEstimation = c1 / c2;
        boolean stationary = stationarityEstimation < STATIONARITY_THRESHOLD;
        if (!stationary && previousResult != null) {
            stationary = Math.abs(stationarityEstimation - previousResult.stationarityEstimation) < 0.01;
        }
        return new Result(stationary, mean, stationarityEstimation);
    }

    public static final class Result {
        public final boolean stationary;
        public final double mean;
        public final double stationarityEstimation;

        public Result(boolean stationary, double mean) {
            this(stationary, mean, -1);
        }

        public Result(boolean stationary, double mean, double stationarityEstimation) {
            this.stationary = stationary;
            this.mean = mean;
            this.stationarityEstimation = stationarityEstimation;
        }
    }
}
