package org.espy.arima;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
=================
1: 0.37
2: 0.40
3: 0.39
4: 0.40
5: 0.39
6: 0.40
7: 0.39
8: 0.39
9: 0.39
=================
*/
public class ForecastAccuracyTest {
    private static final int TEST_PROCESS_QUANTITY = 1000;
    private static final int MIN_FORECAST_QUANTITY = 1;
    private static final int MAX_FORECAST_QUANTITY = 9;
    private static final int OBSERVATION_SIZE = 49;

    private Random random;
    private List<ObservationMetadata> observationMetadatas;
    private Map<Integer, Double> forecastAccuracyRelativeMetrics;

    @Before
    public void doBefore() {
        random = new Random();
        observationMetadatas = new ArrayList<ObservationMetadata>(TEST_PROCESS_QUANTITY);
        for (int i = 0; i < TEST_PROCESS_QUANTITY; i++) {
            ArimaProcessRealization realization = generateArimaProcessRealization();
            observationMetadatas
                    .add(new ObservationMetadata(realization.next(OBSERVATION_SIZE), realization.getArOrder(),
                            realization.getMaOrder()));
        }
        forecastAccuracyRelativeMetrics = new LinkedHashMap<Integer, Double>();
    }

    private ArimaProcessRealization generateArimaProcessRealization() {
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        double[] arCoefficients;
        double[] maCoefficients;
        do {
            arCoefficients = generateCoefficients();
            maCoefficients = generateCoefficients();
        } while (arCoefficients.length == 0 && maCoefficients.length == 0);
        arimaProcess.setArCoefficients(arCoefficients);
        arimaProcess.setMaCoefficients(maCoefficients);
        arimaProcess.setIntegrationOrder(random.nextInt(3));
        arimaProcess.setConstant(2 * random.nextGaussian());
        arimaProcess.setVariation(Math.abs(random.nextGaussian()));
        return new DefaultArimaProcessRealization(arimaProcess);
    }

    private double[] generateCoefficients() {
        double[] coefficients = new double[random.nextInt(3)];
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = random.nextGaussian();
        }
        return coefficients;
    }

    @Test
    public void test() {
        System.out.println("=================");
        for (int i = MIN_FORECAST_QUANTITY; i <= MAX_FORECAST_QUANTITY; i++) {
            test(i);
            double value = forecastAccuracyRelativeMetrics.get(i);
            System.out.println(i + ": " + BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_UP));
        }
        System.out.println("=================\n");
    }

    private void test(int forecastQuantity) {
        double[] metrics = new double[observationMetadatas.size()];
        for (int i = 0; i < observationMetadatas.size(); i++) {
            ObservationMetadata metadata = observationMetadatas.get(i);
            int size = metadata.observations.length - MAX_FORECAST_QUANTITY;
            double[] observations1 = DoubleUtils.copyBegin(metadata.observations, size);
            double[] observations2 = DoubleUtils.copyBegin(metadata.observations, size + forecastQuantity);
            ArimaProcess process = ArimaFitter.fit(observations1, metadata.arOrder, metadata.maOrder);
            ArimaForecaster forecaster = new DefaultArimaForecaster(process, observations1);
            double[] forecast = forecaster.next(forecastQuantity);
            metrics[i] = ForecastAccuracyRelativeMetric.getValue(observations2, forecast);
        }
        forecastAccuracyRelativeMetrics.put(forecastQuantity, ForecastAccuracyRelativeMetric.combine(metrics));
    }

    private static final class ObservationMetadata {
        public final double[] observations;
        public final int arOrder;
        public final int maOrder;

        public ObservationMetadata(double[] observations, int arOrder, int maOrder) {
            this.observations = observations;
            this.arOrder = arOrder;
            this.maOrder = maOrder;
        }
    }
}
