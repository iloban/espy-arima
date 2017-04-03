package org.espy.lab;

import java.util.List;
import java.util.Random;

public class RandomGeneratorChoiceStrategy implements GeneratorChoiceStrategy {

    private final List<TimeSeriesGenerator> generators;

    private final Random random;

    private final int limit;

    private int index;

    public RandomGeneratorChoiceStrategy(GeneratorChoiceContext context) {
        this.generators = context.getGenerators();
        this.random = context.getRandom();
        this.limit = generators.size() * context.getGeneratorUsageCount() - 1;
    }

    @Override public boolean hasNext() {
        return index < limit;
    }

    @Override public TimeSeriesGenerator next() {
        index++;
        return generators.get(random.nextInt(generators.size()));
    }
}
