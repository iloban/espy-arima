package org.espy.arima;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithmArimaFitterStrategy implements ArimaFitterStrategy {
    private static final int CONTROL_OBSERVATION_COUNT = 5;
    private static final int MIN_LEARNING_OBSERVATION_COUNT = 5;
    private static final int MAX_INTEGRATION_ORDER = 2;
    private static final int MAX_AR_ORDER = 2;
    private static final int MAX_MA_ORDER = 2;
    private static final int ITERATION_LIMIT = 100;
    private static final int NON_SIGNIFICANT_IMPROVEMENT_LIMIT = 3;
    private static final double SIGNIFICANT_IMPROVEMENT_THRESHOLD = 1E-3;

    private final double[] learningObservations;
    private final double[] controlObservations;

    private Population population;
    private Individual bestIndividual;
    private int nonSignificantImprovementCount;
    private int iterationCount;

    public GeneticAlgorithmArimaFitterStrategy(double[] observations) {
        int minObservationCount = CONTROL_OBSERVATION_COUNT + MIN_LEARNING_OBSERVATION_COUNT;
        if (observations.length < minObservationCount) {
            throw new IllegalArgumentException(
                    "Observations count is too small (at least need " + minObservationCount + " observations)");
        }
        this.learningObservations =
                DoubleUtils.copyBegin(observations, observations.length - CONTROL_OBSERVATION_COUNT);
        this.controlObservations = DoubleUtils.copyEnd(observations, CONTROL_OBSERVATION_COUNT);
    }

    @Override
    public ArimaProcess fit() {
        fetchInitialPopulation();
        estimatePopulation();
        while (isNotFinishPopulation()) {
            fetchNextGeneration();
            estimatePopulation();
        }
        return getBestArimaProcess();
    }

    private void fetchInitialPopulation() {
        population = new Population();
        for (int d = 0; d <= MAX_INTEGRATION_ORDER; d++) {
            for (int p = 0; p <= MAX_AR_ORDER; p++) {
                for (int q = 0; q <= MAX_MA_ORDER; q++) {
                    if (p == 0 && q == 0) {
                        continue;
                    }
                    ArimaProcess arimaProcess = ArimaFitter.fit(learningObservations, p, d, q);
                    Individual individual = new Individual(arimaProcess);
                    PopulationClass populationClass = new PopulationClass(individual);
                    population.addPopulationClass(populationClass);
                }
            }
        }
        bestIndividual = population.getBestIndividual();
    }

    private void estimatePopulation() {
        population.estimate(learningObservations, controlObservations);
    }

    private boolean isNotFinishPopulation() {
        Individual individual = population.getBestIndividual();
        double difference = bestIndividual.getDifference(individual);
        bestIndividual = Individual.getBestIndividual(bestIndividual, individual);
        if (difference < SIGNIFICANT_IMPROVEMENT_THRESHOLD) {
            nonSignificantImprovementCount++;
        }
        if (nonSignificantImprovementCount >= NON_SIGNIFICANT_IMPROVEMENT_LIMIT) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (++iterationCount >= ITERATION_LIMIT) {
            return false;
        }
        return true;
    }

    private void fetchNextGeneration() {
        population.produceNextGeneration();
    }

    private ArimaProcess getBestArimaProcess() {
        return bestIndividual.getArimaProcess();
    }

    private static final class Population {
        private final List<PopulationClass> populationClasses = new ArrayList<PopulationClass>();

        public void addPopulationClass(PopulationClass populationClass) {
            populationClasses.add(populationClass);
        }

        public Individual getBestIndividual() {
            Individual result = populationClasses.get(0).getBestIndividual();
            for (int i = 1; i < populationClasses.size(); i++) {
                result = Individual.getBestIndividual(result, populationClasses.get(i).getBestIndividual());
            }
            return result;
        }

        public void estimate(double[] learningObservations, double[] controlObservations) {
            for (PopulationClass populationClass : populationClasses) {
                populationClass.estimate(learningObservations, controlObservations);
            }
            Collections.sort(populationClasses);
        }

        public void produceNextGeneration() {
            int groupSize = populationClasses.size() / 3;
            for (int i = 0; i < groupSize; i++) {
                populationClasses.get(i).produceBigGeneration();
            }
            for (int i = groupSize, limit = 2 * groupSize; i < limit; i++) {
                populationClasses.get(i).produceMediumGeneration();
            }
            for (int i = 2 * groupSize; i < populationClasses.size(); i++) {
                populationClasses.get(i).produceSmallGeneration();
            }
        }
    }

    private static final class PopulationClass implements Comparable<PopulationClass> {
        private static final int BIG_GENERATION_SIZE = 20;
        private static final int MEDIUM_GENERATION_SIZE = 10;
        private static final int SMALL_GENERATION_SIZE = 5;

        private final Random random = new Random();
        private final List<Individual> individuals = new ArrayList<Individual>();

        private double estimate;

        public PopulationClass(Individual individual) {
            addIndividual(individual);
            for (int i = 1; i < BIG_GENERATION_SIZE / 2; i++) {
                addIndividual(Mutations.mutateWeakly(individual));
            }
            for (int i = BIG_GENERATION_SIZE / 2; i < BIG_GENERATION_SIZE; i++) {
                addIndividual(Mutations.mutateStrongly(individual));
            }
        }

        public void addIndividual(Individual individual) {
            individuals.add(individual);
        }

        public Individual getBestIndividual() {
            return individuals.get(0);
        }

        public void estimate(double[] learningObservations, double[] controlObservations) {
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

        @Override
        public int compareTo(PopulationClass populationClass) {
            return Double.compare(estimate, populationClass.estimate);
        }

        public void produceBigGeneration() {
            produceGeneration(BIG_GENERATION_SIZE, 10, 5);
        }

        public void produceGeneration(int generationSize, int selectionLimit, int weakMutationLimit) {
            List<Individual> nextGeneration = new ArrayList<Individual>(generationSize);
            performSelection(nextGeneration, selectionLimit);
            performWeakMutation(nextGeneration, weakMutationLimit);
            performStrongMutation(nextGeneration, generationSize);
            individuals.clear();
            individuals.addAll(nextGeneration);
        }

        private void performSelection(List<Individual> nextGeneration, int limit) {
            int level = 0;
            int oldLimit = limit;
            int newLimit;
            do {
                newLimit = oldLimit / 2;
                int size = oldLimit - newLimit;
                if (size >= individuals.size()) {
                    size = individuals.size() - 1;
                }
                for (int i = 0; i < size; i++) {
                    nextGeneration
                            .add(Selectors.performSelection(individuals.get(level), individuals.get(level + i + 1)));
                }
                level++;
                oldLimit = newLimit;
            } while (newLimit > 0);
        }

        private void performWeakMutation(List<Individual> nextGeneration, int limit) {
            int size = limit > individuals.size() ? individuals.size() : limit;
            for (int i = 0; i < size; i++) {
                nextGeneration.add(Mutations.mutateWeakly(individuals.get(i)));
            }
        }

        private void performStrongMutation(List<Individual> nextGeneration, int limit) {
            for (int i = nextGeneration.size(); i < limit; i++) {
                nextGeneration.add(Mutations.mutateStrongly(getRandomIndividual()));
            }
        }

        private Individual getRandomIndividual() {
            return individuals.get(random.nextInt(individuals.size()));
        }

        public void produceMediumGeneration() {
            produceGeneration(MEDIUM_GENERATION_SIZE, 4, 4);
        }

        public void produceSmallGeneration() {
            produceGeneration(SMALL_GENERATION_SIZE, 1, 2);
        }
    }

    private static final class Individual implements Comparable<Individual> {
        private final ArimaProcess arimaProcess;

        private double estimation;

        public Individual(ArimaProcess arimaProcess) {
            this.arimaProcess = arimaProcess;
        }

        public static Individual getBestIndividual(Individual individual1, Individual individual2) {
            double difference = individual1.getDifference(individual2);
            if (difference > 0 || (difference == 0 && individual2.hasLessOrder(individual1))) {
                return individual2;
            }
            return individual1;
        }

        public double getDifference(Individual individual) {
            return estimation - individual.estimation;
        }

        public boolean hasLessOrder(Individual individual) {
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

        public ArimaProcess getArimaProcess() {
            return arimaProcess;
        }

        public void estimate(double[] learningObservations, double[] controlObservations) {
            ArimaForecaster forecaster = new DefaultArimaForecaster(arimaProcess, learningObservations);
            double[] forecast = forecaster.next(CONTROL_OBSERVATION_COUNT);
            estimation = ForecastAccuracyRelativeMetric.getValue(controlObservations, forecast);
        }

        public double getEstimation() {
            return estimation;
        }

        @Override
        public int compareTo(Individual individual) {
            return Double.compare(estimation, individual.estimation);
        }
    }

    private static final class Selectors {
        private Selectors() {
        }

        public static Individual performSelection(Individual individual1, Individual individual2) {
            ArimaProcess arimaProcess1 = individual1.getArimaProcess();
            ArimaProcess arimaProcess2 = individual2.getArimaProcess();
            DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
            arimaProcess.setIntegrationOrder(arimaProcess1.getIntegrationOrder());
            arimaProcess.setArCoefficients(
                    DoubleUtils.getMean(arimaProcess1.getArCoefficients(), arimaProcess2.getArCoefficients()));
            arimaProcess.setMaCoefficients(
                    DoubleUtils.getMean(arimaProcess1.getMaCoefficients(), arimaProcess2.getMaCoefficients()));
            arimaProcess.setVariation(DoubleUtils.getMean(arimaProcess1.getVariation(), arimaProcess2.getVariation()));
            arimaProcess.setConstant(DoubleUtils.getMean(arimaProcess1.getConstant(), arimaProcess2.getConstant()));
            return new Individual(arimaProcess);
        }
    }

    private static final class Mutations {
        private static final Random RANDOM = new Random();
        private static final double[] DELTAS = new double[]{ 0, 0.01, 0.05, 0.1 };
        private static final double[] FACTORS = new double[]{ 0.25, 0.5, 1, 2, 4 };

        private Mutations() {
        }

        public static Individual mutateWeakly(Individual individual) {
            ArimaProcess oldArimaProcess = individual.getArimaProcess();
            DefaultArimaProcess newArimaProcess = new DefaultArimaProcess();
            newArimaProcess.setIntegrationOrder(oldArimaProcess.getIntegrationOrder());
            newArimaProcess.setArCoefficients(mutateWeakly(oldArimaProcess.getArCoefficients()));
            newArimaProcess.setMaCoefficients(mutateWeakly(oldArimaProcess.getMaCoefficients()));
            newArimaProcess.setVariation(oldArimaProcess.getVariation());
            newArimaProcess.setConstant(mutateWeakly(oldArimaProcess.getConstant()));
            return new Individual(newArimaProcess);
        }

        private static double[] mutateWeakly(double[] array) {
            double[] result = new double[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = mutateWeakly(array[i]);
            }
            return result;
        }

        private static double mutateWeakly(double value) {
            return value + getRandomDelta();
        }

        private static double getRandomDelta() {
            return DELTAS[RANDOM.nextInt(DELTAS.length)];
        }

        public static Individual mutateStrongly(Individual individual) {
            ArimaProcess oldArimaProcess = individual.getArimaProcess();
            DefaultArimaProcess newArimaProcess = new DefaultArimaProcess();
            newArimaProcess.setIntegrationOrder(oldArimaProcess.getIntegrationOrder());
            newArimaProcess.setArCoefficients(mutateStrongly(oldArimaProcess.getArCoefficients()));
            newArimaProcess.setMaCoefficients(mutateStrongly(oldArimaProcess.getMaCoefficients()));
            newArimaProcess.setVariation(oldArimaProcess.getVariation());
            newArimaProcess.setConstant(mutateStrongly(oldArimaProcess.getConstant()));
            return new Individual(newArimaProcess);
        }

        private static double[] mutateStrongly(double[] array) {
            double[] result = new double[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = mutateStrongly(array[i]);
            }
            return result;
        }

        private static double mutateStrongly(double value) {
            double newValue = value * getRandomFactor();
            return RANDOM.nextBoolean() ? newValue : -newValue;
        }

        private static double getRandomFactor() {
            return FACTORS[RANDOM.nextInt(FACTORS.length)];
        }
    }
}
