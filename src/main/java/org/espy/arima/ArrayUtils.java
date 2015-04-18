package org.espy.arima;

final class ArrayUtils {
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
}
