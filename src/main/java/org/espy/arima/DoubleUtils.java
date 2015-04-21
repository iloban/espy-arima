package org.espy.arima;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;

import java.util.Arrays;

final class DoubleUtils {
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final double EQUALITY_EPSILON = 1E-10;

    private DoubleUtils() {
    }

    public static boolean isApproximateEqual(double a, double b) {
        return Math.abs(a - b) < EQUALITY_EPSILON;
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

    public static double[] solveSLE(double[][] A, double[] b) {
        DecompositionSolver solver = new QRDecomposition(MatrixUtils.createRealMatrix(A)).getSolver();
        if (solver.isNonSingular()) {
            return solver.solve(MatrixUtils.createRealVector(b)).toArray();
        }
        return b;
    }
}
