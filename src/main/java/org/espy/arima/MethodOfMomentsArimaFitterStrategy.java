package org.espy.arima;

class MethodOfMomentsArimaFitterStrategy implements ArimaFitterStrategy {
    private static final int MAX_INTEGRATION_ORDER = 10;
    private static final double MA_EPSILON = 1E-3;
    private static final double MA_ITERATION_LIMIT = 100;

    private final int arOrder;
    private final int maOrder;
    private final boolean estimateIntegrationOrder;

    private double tau0;
    private double[] observations;
    private double[] acvf;

    // Resulting params
    private int integrationOrder;
    private double constant;
    private double variation;
    private double[] arCoefficients = DoubleUtils.EMPTY_DOUBLE_ARRAY;
    private double[] maCoefficients = DoubleUtils.EMPTY_DOUBLE_ARRAY;

    private Phase[] phases = new Phase[]{ new IntegrationOrderPhase(), new AcvfPhase(), new ArPhase(), new MaPhase() };

    public MethodOfMomentsArimaFitterStrategy(double[] observations, int arOrder, int maOrder) {
        this.observations = observations;
        this.arOrder = arOrder;
        this.maOrder = maOrder;
        this.estimateIntegrationOrder = true;
    }

    public MethodOfMomentsArimaFitterStrategy(double[] observations, int arOrder, int maOrder, int integrationOrder) {
        this.observations = observations;
        this.arOrder = arOrder;
        this.maOrder = maOrder;
        this.integrationOrder = integrationOrder;
        this.estimateIntegrationOrder = false;
    }

    public ArimaProcess fit() {
        for (Phase phase : phases) {
            phase.perform();
        }
        return createArimaProcess();
    }

    private ArimaProcess createArimaProcess() {
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        arimaProcess.setIntegrationOrder(integrationOrder);
        arimaProcess.setConstant(constant);
        arimaProcess.setVariation(variation);
        arimaProcess.setArCoefficients(arCoefficients);
        arimaProcess.setMaCoefficients(maCoefficients);
        return arimaProcess;
    }

    private static interface Phase {
        void perform();
    }

    private final class IntegrationOrderPhase implements Phase {
        @Override
        public void perform() {
            if (estimateIntegrationOrder) {
                fetchIntegrationOrderAndConstant();
            } else {
                fetchConstant();
            }
        }

        private void fetchIntegrationOrderAndConstant() {
            StationarityTest.Result result = StationarityTest.test(observations, null);
            while (!result.stationary && integrationOrder < MAX_INTEGRATION_ORDER) {
                integrationOrder++;
                observations = DoubleUtils.differentiate(observations);
                result = StationarityTest.test(observations, result);
            }
            constant = result.mean;
        }

        private void fetchConstant() {
            constant = 0;
            for (double observation : observations) {
                constant += observation;
            }
            constant /= observations.length;
        }
    }

    private final class AcvfPhase implements Phase {
        @Override
        public void perform() {
            int acvfValueLimit = observations.length;
            int acvfSize = arOrder + maOrder + 1;
            acvf = new double[acvfSize];
            for (int i = 0; i < acvfSize; i++) {
                double acvfValue = 0;
                for (int j = 0; j < acvfValueLimit; j++) {
                    acvfValue += (observations[j] - constant) * (observations[j + i] - constant);
                }
                acvf[i] = acvfValue / observations.length;
                acvfValueLimit--;
            }
        }
    }

    private final class ArPhase implements Phase {
        @Override
        public void perform() {
            if (arOrder > 0) {
                fetchArCoefficients();
                fetchConstant();
            }
        }

        private void fetchArCoefficients() {
            double[][] arMatrix = getArMatrix();
            double[] arVector = getMaVector();
            arCoefficients = DoubleUtils.solveSLE(arMatrix, arVector);
        }

        private double[][] getArMatrix() {
            double[][] result = new double[arOrder][arOrder];
            for (int i = 1; i <= arOrder; i++) {
                for (int j = 1; j <= arOrder; j++) {
                    result[i - 1][j - 1] = acvf[Math.abs(maOrder + i - j)];
                }
            }
            return result;
        }

        private double[] getMaVector() {
            double[] result = new double[arOrder];
            System.arraycopy(acvf, maOrder + 1, result, 0, arOrder);
            return result;
        }

        private void fetchConstant() {
            double factor = 1;
            for (double arCoefficient : arCoefficients) {
                factor -= arCoefficient;
            }
            constant *= factor;
        }
    }

    private final class MaPhase implements Phase {
        @Override
        public void perform() {
            if (maOrder > 0) {
                fetchMaCoefficients();
                fetchMaVariation();
            } else {
                fetchArVariation();
            }
        }

        private void fetchMaCoefficients() {
            double[] modifiedAcvf = getModifiedAcvf();
            double[] tau = getInitialTau(modifiedAcvf[0]);
            double[] f = getF(tau, modifiedAcvf);
            int iterationCount = 0;

            do {
                double[][] T = getT(tau);
                double[] h = getH(T, f);
                tau = getTau(tau, h);
                f = getF(tau, modifiedAcvf);

                if (++iterationCount >= MA_ITERATION_LIMIT) {
                    break;
                }
            } while (checkF(f));

            tau0 = tau[0];
            maCoefficients = getMaCoefficientEstimation(tau);
        }

        private double[] getModifiedAcvf() {
            if (arOrder == 0) {
                return acvf;
            }
            double[] modifiedAcvf = new double[maOrder + 1];
            for (int i = 0; i <= maOrder; i++) {
                modifiedAcvf[i] = getModifiedAcvf(i);
            }
            return modifiedAcvf;
        }

        private double getModifiedAcvf(int i) {
            double result = 0;
            for (int j = 0; j <= arOrder; j++) {
                for (int k = 0; k <= arOrder; k++) {
                    double ar1 = j == 0 ? -1 : arCoefficients[j - 1];
                    double ar2 = k == 0 ? -1 : arCoefficients[k - 1];
                    result += ar1 * ar2 * acvf[Math.abs(i + j - k)];
                }
            }
            return result;
        }

        public double[] getInitialTau(double modifiedAcvf0) {
            double[] result = new double[maOrder + 1];
            result[0] = Math.sqrt(modifiedAcvf0);
            return result;
        }

        private double[] getF(double[] tau, double[] modifiedAcvf) {
            double[] result = new double[maOrder + 1];
            for (int i = 0; i <= maOrder; i++) {
                result[i] = getF(tau, modifiedAcvf[i], i);
            }
            return result;
        }

        private double getF(double[] tau, double modifiedAcvfValue, int i) {
            double result = 0;
            for (int j = 0; j <= maOrder - i; j++) {
                result += tau[j] * tau[i + j];
            }
            return result - modifiedAcvfValue;
        }

        private double[][] getT(double[] tau) {
            double[][] result = new double[maOrder + 1][];
            for (int i = 0; i <= maOrder; i++) {
                result[i] = new double[maOrder + 1];
                for (int j = 0; j <= maOrder; j++) {
                    double tau1 = j + i > maOrder ? 0 : tau[j + i];
                    double tau2 = j - i < 0 ? 0 : tau[j - i];
                    result[i][j] = tau1 + tau2;
                }
            }
            return result;
        }

        private double[] getH(double[][] T, double[] f) {
            return DoubleUtils.solveSLE(T, f);
        }

        private double[] getTau(double[] tau, double[] h) {
            double[] result = new double[maOrder + 1];
            for (int i = 0; i <= maOrder; i++) {
                result[i] = tau[i] - h[i];
            }
            return result;
        }

        private boolean checkF(double[] f) {
            for (double fValue : f) {
                if (Math.abs(fValue) > MA_EPSILON) {
                    return true;
                }
            }
            return false;
        }

        private double[] getMaCoefficientEstimation(double[] tau) {
            double[] result = new double[maOrder];
            for (int i = 0; i < maOrder; i++) {
                result[i] = -tau[i + 1] / tau[0];
            }
            return result;
        }

        private void fetchMaVariation() {
            variation = tau0 * tau0;
        }

        private void fetchArVariation() {
            variation = acvf[0];
            for (int i = 0; i < arCoefficients.length; i++) {
                variation -= arCoefficients[i] * acvf[i + 1];
            }
        }
    }
}
