package org.espy.arima;

import org.junit.Test;

import java.util.Arrays;

import static org.espy.arima.TestUtils.EPS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DefaultArimaProcessRealizationTest {
    private static void test(double[] arCoefficients, int integratedOrder, double[] maCoefficients, double variance,
            int size) {
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        arimaProcess.setArCoefficients(arCoefficients);
        arimaProcess.setMaCoefficients(maCoefficients);
        arimaProcess.setIntegratedOrder(integratedOrder);
        arimaProcess.setVariance(variance);
        ArimaProcessRealization arimaProcessRealization = new DefaultArimaProcessRealization(arimaProcess);

        assertEquals(integratedOrder, arimaProcessRealization.getIntegratedOrder());
        assertEquals(variance, arimaProcessRealization.getVariance(), EPS);
        assertEquals(0, arimaProcessRealization.getConstant(), EPS);
        assertArrayEquals(arCoefficients, arimaProcessRealization.getArCoefficients(), EPS);
        assertArrayEquals(maCoefficients, arimaProcessRealization.getMaCoefficients(), EPS);

        System.out.println("--------------------");
        System.out.println("Process: " + arimaProcessRealization);
        System.out.println("Observations: " + Arrays.toString(arimaProcessRealization.next(size)));
        System.out.println("--------------------");
    }

    @Test
    public void test() {
        // test(new double[]{ -0.7, 0.3 }, 0, new double[0], 1E-1, 50);
        // test(new double[0], 0, new double[]{ -0.7, 0.3 }, 1E-1, 50);
        // test(new double[]{ -0.7, 0.3 }, 0, new double[]{ -0.7, 0.3 }, 1E-1, 50);
        // test(new double[]{ -0.7, 0.3 }, 3, new double[]{ -0.7, 0.3 }, 1E-1, 50);
    }
}
