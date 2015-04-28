package org.espy.arima;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ForecastAccuracyTest {
    private static final int MAX_INTEGRATION_ORDER = 2;
    private static final int MAX_AR_ORDER = 2;
    private static final int MAX_MA_ORDER = 2;
    private static final int TEST_PROCESS_QUANTITY = 10;
    private static final int MIN_FORECAST_QUANTITY = 1;
    private static final int MAX_FORECAST_QUANTITY = 10;
    private static final int OBSERVATION_SIZE = 50;

    private Random random;
    private Map<Integer, Double> forecastAccuracyRelativeMetrics1;
    private Map<Integer, Double> forecastAccuracyRelativeMetrics2;
    private List<ObservationMetadata> observationMetadataList;

    @Test
    public void test() {
        random = new Random();
        forecastAccuracyRelativeMetrics1 = new LinkedHashMap<Integer, Double>();
        forecastAccuracyRelativeMetrics2 = new LinkedHashMap<Integer, Double>();
        observationMetadataList = new ArrayList<ObservationMetadata>();
        int size = OBSERVATION_SIZE - MAX_FORECAST_QUANTITY;
        for (int d = 0; d <= MAX_INTEGRATION_ORDER; d++) {
            for (int p = 0; p <= MAX_AR_ORDER; p++) {
                for (int q = 0; q <= MAX_MA_ORDER; q++) {
                    if (p == 0 && q == 0) {
                        continue;
                    }
                    for (int i = 0; i < TEST_PROCESS_QUANTITY; i++) {
                        ArimaProcessRealization realization = generateArimaProcessRealization(p, d, q);
                        double[] observations = realization.next(OBSERVATION_SIZE);
                        double[] observations1 = DoubleUtils.copyBegin(observations, size);

                        ArimaProcess process1 = ArimaFitter.fit(observations1);
                        ArimaForecaster forecaster1 = new DefaultArimaForecaster(process1, observations1);
                        double[] forecast1 = forecaster1.next(MAX_FORECAST_QUANTITY);

                        ArimaProcess process2 =
                                ArimaFitter.fit(observations1, realization.getArOrder(), realization.getMaOrder());
                        ArimaForecaster forecaster2 = new DefaultArimaForecaster(process2, observations1);
                        double[] forecast2 = forecaster2.next(MAX_FORECAST_QUANTITY);

                        ObservationMetadata observationMetadata = new ObservationMetadata();
                        observationMetadata.observations = observations;
                        observationMetadata.forecast1 = forecast1;
                        observationMetadata.forecast2 = forecast2;
                        observationMetadataList.add(observationMetadata);
                    }
                }
            }
        }

        System.out.println("=============");
        System.out.println("Count: " + observationMetadataList.size());
        System.out.println("====(1)==(2)=");
        for (int i = MIN_FORECAST_QUANTITY; i <= MAX_FORECAST_QUANTITY; i++) {
            test(i);
            double value1 = forecastAccuracyRelativeMetrics1.get(i);
            double value2 = forecastAccuracyRelativeMetrics2.get(i);
            System.out.println(
                    (i < 10 ? " " + i : i) + ": " + BigDecimal.valueOf(value1).setScale(2, BigDecimal.ROUND_UP) + " "
                            + BigDecimal.valueOf(value2).setScale(2, BigDecimal.ROUND_UP));
        }
        System.out.println("=============");
    }

    private ArimaProcessRealization generateArimaProcessRealization(int p, int d, int q) {
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        arimaProcess.setArCoefficients(generateCoefficients(p));
        arimaProcess.setMaCoefficients(generateCoefficients(q));
        arimaProcess.setIntegrationOrder(d);
        arimaProcess.setConstant(2 * random.nextGaussian());
        arimaProcess.setVariation(Math.abs(random.nextGaussian()));
        return new DefaultArimaProcessRealization(arimaProcess);
    }

    private double[] generateCoefficients(int count) {
        double[] coefficients = new double[count];
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = random.nextGaussian();
        }
        return coefficients;
    }

    private void test(int forecastQuantity) {
        double[] metrics1 = new double[observationMetadataList.size()];
        double[] metrics2 = new double[observationMetadataList.size()];
        int size = OBSERVATION_SIZE - MAX_FORECAST_QUANTITY;
        for (int i = 0; i < observationMetadataList.size(); i++) {
            ObservationMetadata metadata = observationMetadataList.get(i);
            double[] observations2 = DoubleUtils.copyBegin(metadata.observations, size + forecastQuantity);
            double[] forecast1 = DoubleUtils.copyBegin(metadata.forecast1, forecastQuantity);
            double[] forecast2 = DoubleUtils.copyBegin(metadata.forecast2, forecastQuantity);
            metrics1[i] = ForecastAccuracyRelativeMetric.getValue(observations2, forecast1);
            metrics2[i] = ForecastAccuracyRelativeMetric.getValue(observations2, forecast2);
        }
        forecastAccuracyRelativeMetrics1.put(forecastQuantity, ForecastAccuracyRelativeMetric.combine(metrics1));
        forecastAccuracyRelativeMetrics2.put(forecastQuantity, ForecastAccuracyRelativeMetric.combine(metrics2));
    }

    private static final class ObservationMetadata {
        public double[] observations;
        public double[] forecast1;
        public double[] forecast2;
    }
}
