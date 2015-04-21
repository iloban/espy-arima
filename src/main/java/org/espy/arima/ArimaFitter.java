package org.espy.arima;

public final class ArimaFitter {
    private ArimaFitter() {
    }

    public static ArimaProcess fit(double[] observations, int arOrder, int integrationOrder, int maOrder) {
        ArimaFitterStrategy arimaFitterStrategy =
                new MethodOfMomentsArimaFitterStrategy(observations, arOrder, maOrder, integrationOrder);
        return fit(arimaFitterStrategy);
    }

    private static ArimaProcess fit(ArimaFitterStrategy arimaFitterStrategy) {
        return arimaFitterStrategy.fit();
    }

    public static ArimaProcess fit(double[] observations, int arOrder, int maOrder) {
        ArimaFitterStrategy arimaFitterStrategy =
                new MethodOfMomentsArimaFitterStrategy(observations, arOrder, maOrder);
        return fit(arimaFitterStrategy);
    }
}
