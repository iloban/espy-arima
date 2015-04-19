package org.espy.arima;

import java.util.Arrays;

final class ArrayUtils {
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];

    private ArrayUtils() {
    }

    public static double getLast(double[] array) {
        return array[array.length - 1];
    }

    public static void appendWithShift(double[] array, double value) {
        if (array.length > 0) {
            System.arraycopy(array, 1, array, 0, array.length - 1);
            setLast(array, value);
        }
    }

    public static void setLast(double[] array, double value) {
        array[array.length - 1] = value;
    }

    public static double[] differentiate(double[] array) {
        double[] result = new double[array.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = array[i + 1] - array[i];
        }
        return result;
    }

    public static double[] copyFromEnd(double[] array, int size) {
        return Arrays.copyOfRange(array, array.length - size, array.length);
    }
}
