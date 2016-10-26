package org.espy.lab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeSeriesSuiteFactory {

    public static TimeSeriesSuite createTimeSeriesSuite(TimeSeriesSuiteConfiguration configuration) {
        List<TimeSeriesGenerator> generators = configuration.getGenerators();
        ChoiceStrategy choiceStrategy = createChoiceStrategy(configuration);
        Random random = new Random(configuration.getSeed());
        ChoiceContext choiceContext = ChoiceContext.builder()
                .setGenerators(generators)
                .setRandom(random)
                .build();
        GeneratorContext generatorContext = GeneratorContext.builder()
                .setRandom(random)
                .build();
        List<TimeSeriesSample> samples = new ArrayList<>();
        int size = configuration.getGeneratorUsageCount() * generators.size();
        for (int i = 0; i < size; i++) {
            TimeSeriesGenerator generator = choiceStrategy.select(choiceContext);
            samples.add(generator.generate(generatorContext));
        }
        return new TimeSeriesSuite(samples);
    }

    private static ChoiceStrategy createChoiceStrategy(TimeSeriesSuiteConfiguration configuration) {
        switch (configuration.getChoiceStrategyType()) {
            case RANDOM:
                return new RandomChoiceStrategy();
            default:
                throw new IllegalArgumentException("No case for enum value " + configuration.getChoiceStrategyType());
        }
    }
}
