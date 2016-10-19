package org.espy.arima;

import org.junit.Test;

import java.util.Arrays;

import static org.espy.arima.DoubleUtils.EQUALITY_EPSILON;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DefaultArimaProcessRealizationTest {
    @Test
    public void test() {
        // test(new double[]{ -0.7, 0.3 }, 0, new double[0], 1E-1, 50);
        // test(new double[0], 0, new double[]{ -0.7, 0.3 }, 1E-1, 50);
        // test(new double[]{ -0.7, 0.3 }, 0, new double[]{ -0.7, 0.3 }, 1E-1, 50);
        test(new double[]{ -0.7, 0.3 }, 3, new double[]{ -0.7, 0.3 }, 1E-1, 50);
    }

    private static void test(double[] arCoefficients, int integrationOrder, double[] maCoefficients, double variation,
            int size) {
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        arimaProcess.setArCoefficients(arCoefficients);
        arimaProcess.setMaCoefficients(maCoefficients);
        arimaProcess.setIntegrationOrder(integrationOrder);
        arimaProcess.setVariation(variation);
        ArimaProcessRealization arimaProcessRealization = new DefaultArimaProcessRealization(arimaProcess);

        assertEquals(integrationOrder, arimaProcessRealization.getIntegrationOrder());
        assertEquals(0, arimaProcessRealization.getExpectation(), EQUALITY_EPSILON);
        assertEquals(variation, arimaProcessRealization.getVariation(), EQUALITY_EPSILON);
        assertEquals(0, arimaProcessRealization.getConstant(), EQUALITY_EPSILON);
        assertArrayEquals(arCoefficients, arimaProcessRealization.getArCoefficients(), EQUALITY_EPSILON);
        assertArrayEquals(maCoefficients, arimaProcessRealization.getMaCoefficients(), EQUALITY_EPSILON);

        System.out.println("--------------------");
        System.out.println("Process: " + arimaProcessRealization);
        System.out.println("Observations: " + Arrays.toString(arimaProcessRealization.next(size)));
        System.out.println("--------------------");
    }
}
