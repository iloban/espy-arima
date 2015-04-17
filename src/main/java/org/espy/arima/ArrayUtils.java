package org.espy.arima;

public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static void shiftToLeft(double[] array) {
        if (array.length > 0) {
            System.arraycopy(array, 1, array, 0, array.length - 1);
        }
    }
}
