package org.espy.arima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithmArimaFitterStrategy implements ArimaFitterStrategy {

    private static final int CONTROL_OBSERVATIONS_COUNT = 10;

    private static final int MIN_LEARNING_OBSERVATIONS_COUNT = 5;

    private static final int MAX_INTEGRATION_ORDER = 2;

    private static final int MAX_AR_ORDER = 3;

    private static final int MAX_MA_ORDER = 3;

    private static final int ITERATION_LIMIT = 100;

    private static final int NON_SIGNIFICANT_IMPROVEMENT_LIMIT = 5;

    private static final double SIGNIFICANT_IMPROVEMENT_THRESHOLD = 1E-3;

    private final Random random;

    private final InnerArimaForecaster innerForecaster;

    private final double[] learningObservations;

    private final double[] controlObservations;

    private Population population;

    private Individual bestIndividual;

    private int nonSignificantImprovementCount;

    private int iterationCount;

    GeneticAlgorithmArimaFitterStrategy(double[] observations) {
        this(observations, new Random(), new DefaultInnerArimaForecaster());
    }

    public GeneticAlgorithmArimaFitterStrategy(double[] observations, Random random, InnerArimaForecaster innerForecaster) {
        int minObservationCount = CONTROL_OBSERVATIONS_COUNT + MIN_LEARNING_OBSERVATIONS_COUNT;
        if (observations.length < minObservationCount) {
            throw new IllegalArgumentException(
                    "Observations count is too small (at least need " + minObservationCount + " observations)");
        }
        this.learningObservations = DoubleUtils.copyBegin(
                observations,
                observations.length - CONTROL_OBSERVATIONS_COUNT
        );
        this.controlObservations = DoubleUtils.copyEnd(observations, CONTROL_OBSERVATIONS_COUNT);
        this.random = random;
        this.innerForecaster = innerForecaster;
    }

    @Override public ArimaProcess fit() {
        initPopulation();
        estimatePopulation();
        while (!isFinishPopulation()) {
            nextPopulation();
            estimatePopulation();
        }
        return getBestIndividual();
    }

    private void initPopulation() {
        population = new Population();
        for (int d = 0; d <= MAX_INTEGRATION_ORDER; d++) {
            for (int p = 0; p <= MAX_AR_ORDER; p++) {
                for (int q = 0; q <= MAX_MA_ORDER; q++) {
                    if (p == 0 && q == 0) {
                        continue;
                    }
                    MethodOfMomentsArimaFitterStrategy initialFitter = new MethodOfMomentsArimaFitterStrategy(learningObservations, p, d, q);
                    ArimaProcess arimaProcess = initialFitter.fit();
                    Individual individual = new Individual(innerForecaster, arimaProcess);
                    PopulationClass populationClass = new PopulationClass(individual, Population.BIG_GENERATION, random, innerForecaster);
                    population.addPopulationClass(populationClass);
                }
            }
        }
        bestIndividual = population.getBestIndividual();
    }

    private void estimatePopulation() {
        population.estimate(learningObservations, controlObservations);
    }

    private boolean isFinishPopulation() {
        Individual individual = population.getBestIndividual();
        double difference = bestIndividual.getDifference(individual);
        bestIndividual = Individual.getBestIndividual(bestIndividual, individual);
        if (difference < SIGNIFICANT_IMPROVEMENT_THRESHOLD) {
            nonSignificantImprovementCount++;
        }
        if (nonSignificantImprovementCount >= NON_SIGNIFICANT_IMPROVEMENT_LIMIT) {
            return true;
        }
        if (++iterationCount >= ITERATION_LIMIT) {
            return true;
        }
        return false;
    }

    private void nextPopulation() {
        population.nextGeneration();
    }

    private ArimaProcess getBestIndividual() {
        return bestIndividual.getArimaProcess();
    }

    public interface InnerArimaForecaster {

        double[] forecast(ArimaProcess arimaProcess, double[] observedPart, int forecastLength);
    }

    private static final class Population {

        static final GenerationSettings BIG_GENERATION = new GenerationSettings(16, 4, 4);

        static final GenerationSettings MEDIUM_GENERATION = new GenerationSettings(8, 2, 2);

        static final GenerationSettings SMALL_GENERATION = new GenerationSettings(4, 1, 1);

        final List<PopulationClass> populationClasses = new ArrayList<PopulationClass>();

        void addPopulationClass(PopulationClass populationClass) {
            populationClasses.add(populationClass);
        }

        Individual getBestIndividual() {
            Individual result = populationClasses.get(0).getBestIndividual();
            for (int i = 1; i < populationClasses.size(); i++) {
                result = Individual.getBestIndividual(result, populationClasses.get(i).getBestIndividual());
            }
            return result;
        }

        void estimate(double[] learningObservations, double[] controlObservations) {
            for (PopulationClass populationClass : populationClasses) {
                populationClass.estimate(learningObservations, controlObservations);
            }
            Collections.sort(populationClasses);
        }

        void nextGeneration() {
            int groupSize = populationClasses.size() / 3;
            for (int i = 0; i < groupSize; i++) {
                populationClasses.get(i).produceGeneration(BIG_GENERATION);
            }
            for (int i = groupSize, limit = 2 * groupSize; i < limit; i++) {
                populationClasses.get(i).produceGeneration(MEDIUM_GENERATION);
            }
            for (int i = 2 * groupSize; i < populationClasses.size(); i++) {
                populationClasses.get(i).produceGeneration(SMALL_GENERATION);
            }
        }

        private static final class GenerationSettings {

            final int size;

            final int selectionsCount;

            final int weakMutationsCount;

            GenerationSettings(int size, int selectionsCount, int weakMutationsCount) {
                this.size = size;
                this.selectionsCount = selectionsCount;
                this.weakMutationsCount = weakMutationsCount;
            }
        }
    }

    private static final class PopulationClass implements Comparable<PopulationClass> {

        static final double[] DELTAS = new double[]{0, 0.01, 0.05, 0.1};

        static final double[] FACTORS = new double[]{0.25, 0.5, 1, 2, 4};

        final Random random;

        final InnerArimaForecaster innerForecaster;

        final List<Individual> individuals = new ArrayList<Individual>();

        double estimate;

        PopulationClass(Individual individual, Population.GenerationSettings generation, Random random,
                        InnerArimaForecaster innerForecaster) {
            this.random = random;
            this.innerForecaster = innerForecaster;
            addIndividual(individual);
            for (int i = 1; i < generation.size / 2; i++) {
                addIndividual(mutateWeakly(individual));
            }
            for (int i = generation.size / 2; i < generation.size; i++) {
                addIndividual(mutateStrongly(individual));
            }
        }

        void addIndividual(Individual individual) {
            individuals.add(individual);
        }

        Individual getBestIndividual() {
            return individuals.get(0);
        }

        void estimate(double[] learningObservations, double[] controlObservations) {
            for (Individual individual : individuals) {
                individual.estimate(learningObservations, controlObservations);
            }
            Collections.sort(individuals);
            double[] estimations = new double[individuals.size()];
            for (int i = 0; i < individuals.size(); i++) {
                estimations[i] = individuals.get(i).getEstimation();
            }
            estimate = ForecastAccuracyRelativeMetric.combineSorted(estimations);
        }

        @Override public int compareTo(PopulationClass populationClass) {
            return Double.compare(estimate, populationClass.estimate);
        }

        void produceGeneration(Population.GenerationSettings generationSettings) {
            List<Individual> nextGeneration = new ArrayList<Individual>(generationSettings.size);
            performSelection(nextGeneration, generationSettings.selectionsCount);
            performWeakMutation(nextGeneration, generationSettings.weakMutationsCount);
            performStrongMutation(nextGeneration, generationSettings.size);
            individuals.clear();
            individuals.addAll(nextGeneration);
        }

        void performSelection(List<Individual> nextGeneration, int limit) {
            int level = 0;
            int oldLimit = limit;
            int newLimit;
            do {
                newLimit = oldLimit / 2;
                int count = oldLimit - newLimit;
                if (count >= individuals.size() - level) {
                    count = individuals.size() - level - 2;
                }
                for (int i = 0; i < count; i++) {
                    nextGeneration.add(performSelection(individuals.get(level), individuals.get(level + i + 1)));
                }
                level++;
                oldLimit = newLimit;
            } while (newLimit > 0);
        }

        Individual performSelection(Individual individual1, Individual individual2) {
            ArimaProcess arimaProcess1 = individual1.getArimaProcess();
            ArimaProcess arimaProcess2 = individual2.getArimaProcess();
            DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
            arimaProcess.setIntegrationOrder(arimaProcess1.getIntegrationOrder());
            arimaProcess.setArCoefficients(
                    DoubleUtils.getMean(arimaProcess1.getArCoefficients(), arimaProcess2.getArCoefficients()));
            arimaProcess.setMaCoefficients(
                    DoubleUtils.getMean(arimaProcess1.getMaCoefficients(), arimaProcess2.getMaCoefficients()));
            arimaProcess.setShockVariation(DoubleUtils.getMean(arimaProcess1.getShockVariation(), arimaProcess2.getShockVariation()));
            arimaProcess.setConstant(DoubleUtils.getMean(arimaProcess1.getConstant(), arimaProcess2.getConstant()));
            return new Individual(innerForecaster, arimaProcess);
        }

        void performWeakMutation(List<Individual> nextGeneration, int limit) {
            int size = limit > individuals.size() ? individuals.size() : limit;
            for (int i = 0; i < size; i++) {
                nextGeneration.add(mutateWeakly(individuals.get(i)));
            }
        }

        void performStrongMutation(List<Individual> nextGeneration, int limit) {
            for (int i = nextGeneration.size(); i < limit; i++) {
                nextGeneration.add(mutateStrongly(getRandomIndividual()));
            }
        }

        Individual getRandomIndividual() {
            return individuals.get(random.nextInt(individuals.size()));
        }

        Individual mutateWeakly(Individual individual) {
            ArimaProcess oldArimaProcess = individual.getArimaProcess();
            DefaultArimaProcess newArimaProcess = new DefaultArimaProcess();
            newArimaProcess.setIntegrationOrder(oldArimaProcess.getIntegrationOrder());
            newArimaProcess.setArCoefficients(mutateWeakly(oldArimaProcess.getArCoefficients()));
            newArimaProcess.setMaCoefficients(mutateWeakly(oldArimaProcess.getMaCoefficients()));
            newArimaProcess.setShockVariation(oldArimaProcess.getShockVariation());
            newArimaProcess.setConstant(mutateWeakly(oldArimaProcess.getConstant()));
            return new Individual(innerForecaster, newArimaProcess);
        }

        double[] mutateWeakly(double[] array) {
            double[] result = new double[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = mutateWeakly(array[i]);
            }
            normalize(result);
            return result;
        }

        void normalize(double[] values) {
            double sum = 0;
            for (double value : values) {
                sum += Math.abs(value);
            }
            if (sum < 1) {
                return;
            }
            double ratio = 1 - (sum - 1) / sum;
            for (int i = 0; i < values.length; i++) {
                values[i] *= ratio;
                if (Math.abs(values[i]) > 0.01) {
                    values[i] = Math.signum(values[i]) * 0.01;
                }
            }
        }

        double mutateWeakly(double value) {
            double newValue = value + getRandomDelta();
            if (Math.abs(newValue) < 0.001) {
                newValue = Math.signum(newValue) * 0.001;
            } else if (Math.abs(newValue) > 0.999) {
                newValue = Math.signum(newValue) * 0.999;
            }
            return newValue;
        }

        double getRandomDelta() {
            double delta = DELTAS[random.nextInt(DELTAS.length)];
            return random.nextBoolean() ? delta : -delta;
        }

        Individual mutateStrongly(Individual individual) {
            ArimaProcess oldArimaProcess = individual.getArimaProcess();
            DefaultArimaProcess newArimaProcess = new DefaultArimaProcess();
            newArimaProcess.setIntegrationOrder(oldArimaProcess.getIntegrationOrder());
            newArimaProcess.setArCoefficients(mutateStrongly(oldArimaProcess.getArCoefficients()));
            newArimaProcess.setMaCoefficients(mutateStrongly(oldArimaProcess.getMaCoefficients()));
            newArimaProcess.setShockVariation(oldArimaProcess.getShockVariation());
            newArimaProcess.setConstant(mutateStrongly(oldArimaProcess.getConstant()));
            return new Individual(innerForecaster, newArimaProcess);
        }

        double[] mutateStrongly(double[] array) {
            double[] result = new double[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = mutateStrongly(array[i]);
            }
            normalize(result);
            return result;
        }

        double mutateStrongly(double value) {
            double newValue = value * getRandomFactor();
            if (Math.abs(newValue) < 0.001) {
                newValue = Math.signum(newValue) * 0.001;
            } else if (Math.abs(newValue) > 0.999) {
                newValue = Math.signum(newValue) * 0.999;
            }
            return random.nextBoolean() ? newValue : -newValue;
        }

        double getRandomFactor() {
            return FACTORS[random.nextInt(FACTORS.length)];
        }
    }

    private static final class Individual implements Comparable<Individual> {

        final InnerArimaForecaster innerForecaster;

        final ArimaProcess arimaProcess;

        double estimation;

        Individual(InnerArimaForecaster innerForecaster, ArimaProcess arimaProcess) {
            this.innerForecaster = innerForecaster;
            this.arimaProcess = arimaProcess;
        }

        static Individual getBestIndividual(Individual individual1, Individual individual2) {
            double difference = individual1.getDifference(individual2);
            if (difference > 0 || (difference == 0 && individual2.hasLessOrder(individual1))) {
                return individual2;
            }
            return individual1;
        }

        double getDifference(Individual individual) {
            return estimation - individual.estimation;
        }

        boolean hasLessOrder(Individual individual) {
            int difference = arimaProcess.getArOrder() - individual.arimaProcess.getArOrder();
            if (difference < 0) {
                return true;
            } else if (difference > 0) {
                return false;
            }
            difference = arimaProcess.getMaOrder() - individual.arimaProcess.getMaOrder();
            if (difference < 0) {
                return true;
            } else if (difference > 0) {
                return false;
            }
            difference = arimaProcess.getIntegrationOrder() - individual.arimaProcess.getIntegrationOrder();
            if (difference < 0) {
                return true;
            } else if (difference > 0) {
                return false;
            }
            return false;
        }

        ArimaProcess getArimaProcess() {
            return arimaProcess;
        }

        void estimate(double[] learningObservations, double[] controlObservations) {
            double[] forecast = innerForecaster.forecast(arimaProcess, learningObservations, CONTROL_OBSERVATIONS_COUNT);
            estimation = ForecastAccuracyRelativeMetric.getValue(controlObservations, forecast);
        }

        double getEstimation() {
            return estimation;
        }

        @Override public int compareTo(Individual individual) {
            return Double.compare(estimation, individual.estimation);
        }
    }

    public static final class DefaultInnerArimaForecaster implements InnerArimaForecaster {

        @Override public double[] forecast(ArimaProcess arimaProcess, double[] observations, int forecastLength) {
            return new DefaultArimaForecaster(arimaProcess, observations).next(forecastLength);
        }

        @Override public String toString() {
            return "default";
        }
    }
}
