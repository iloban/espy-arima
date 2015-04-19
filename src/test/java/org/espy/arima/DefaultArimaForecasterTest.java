package org.espy.arima;

import org.junit.Test;

import java.util.Arrays;

public class DefaultArimaForecasterTest {
    @Test
    public void test() {
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        arimaProcess.setMaCoefficients(0.7, -0.3, 0.1);
        arimaProcess.setIntegrationOrder(1);

        double[] observations =
                new double[]{ -0.262502, -1.18863, -0.874338, 0.587444, 1.37453, 1.8149, 1.87319, 1.32972, 2.13976,
                              4.93046, 5.24314, 3.38584, 3.1152, 2.84794, 1.04411, 1.5423, 1.85116, 0.968863, -0.173814,
                              -3.43235 };

        ArimaForecaster arimaForecaster = new DefaultArimaForecaster(arimaProcess, observations);
        double[] forecast = arimaForecaster.next(10);

        System.out.println("Forecast: " + Arrays.toString(forecast));
    }
}
