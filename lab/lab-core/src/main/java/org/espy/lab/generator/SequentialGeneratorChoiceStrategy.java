package org.espy.lab.generator;

import java.util.List;

public class SequentialGeneratorChoiceStrategy implements GeneratorChoiceStrategy {

    private final List<TimeSeriesGenerator> generators;

    private final int limit;

    private final int generatorUsageCount;

    private int count;

    public SequentialGeneratorChoiceStrategy(GeneratorChoiceContext context) {
        this.generators = context.getGenerators();
        this.generatorUsageCount = context.getGeneratorUsageCount();
        this.limit = generators.size() * generatorUsageCount - 1;
    }

    @Override public boolean hasNext() {
        return count < limit;
    }

    @Override public TimeSeriesGenerator next() {
        int index = count / generatorUsageCount;
        TimeSeriesGenerator generator = generators.get(index);
        count++;
        return generator;
    }
}
