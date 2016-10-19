package org.espy.arima;

public final class ArimaFitter {
    private ArimaFitter() {
    }

    public static ArimaProcess fit(double[] observations, int arOrder, int integrationOrder, int maOrder) {
        return fit(new MethodOfMomentsArimaFitterStrategy(observations, arOrder, maOrder, integrationOrder));
    }

    private static ArimaProcess fit(ArimaFitterStrategy arimaFitterStrategy) {
        return arimaFitterStrategy.fit();
    }

    public static ArimaProcess fit(double[] observations, int arOrder, int maOrder) {
        return fit(new MethodOfMomentsArimaFitterStrategy(observations, arOrder, maOrder));
    }

    public static ArimaProcess fit(double[] observations) {
        return fit(new GeneticAlgorithmArimaFitterStrategy(observations));
    }
}
