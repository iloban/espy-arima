package org.espy.lab.suite;

import org.espy.lab.generator.*;
import org.espy.lab.sample.TimeSeriesSample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TimeSeriesSuiteFactory {

    private TimeSeriesSuiteFactory() {
    }

    public static TimeSeriesSuite createTimeSeriesSuite(TimeSeriesSuiteConfiguration configuration) {
        Random random = new Random(configuration.getSeed());
        GeneratorChoiceContext generatorChoiceContext = createGeneratorChoiceContext(configuration, random);
        GeneratorChoiceStrategy generatorChoiceStrategy = createGeneratorChoiceStrategy(generatorChoiceContext);
        GeneratorContext generatorContext = createGeneratorContext(random);
        List<TimeSeriesSample> samples = new ArrayList<>();
        while (generatorChoiceStrategy.hasNext()) {
            TimeSeriesGenerator generator = generatorChoiceStrategy.next();
            samples.add(generator.generate(generatorContext));
        }
        return new TimeSeriesSuite(samples);
    }

    private static GeneratorChoiceStrategy createGeneratorChoiceStrategy(GeneratorChoiceContext context) {
        switch (context.getGeneratorChoiceStrategyType()) {
            case SEQUENTIAL:
                return new SequentialGeneratorChoiceStrategy(context);
            case RANDOM:
                return new RandomGeneratorChoiceStrategy(context);
            default:
                throw new IllegalArgumentException("Unexpected strategy type " + context.getGeneratorChoiceStrategyType());
        }
    }

    private static GeneratorChoiceContext createGeneratorChoiceContext(TimeSeriesSuiteConfiguration configuration,
                                                                       Random random) {
        return GeneratorChoiceContext.builder()
                .setRandom(random)
                .setGenerators(configuration.getGenerators())
                .setGeneratorUsageCount(configuration.getGeneratorUsageCount())
                .setGeneratorChoiceStrategyType(configuration.getGeneratorChoiceStrategyType())
                .build();
    }

    private static GeneratorContext createGeneratorContext(Random random) {
        return GeneratorContext.builder()
                .setRandom(random)
                .build();
    }
}
