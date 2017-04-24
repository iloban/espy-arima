package org.espy.lab.generator;

import java.util.List;
import java.util.Random;

public final class GeneratorChoiceContext {

    private final Random random;

    private final List<TimeSeriesGenerator> generators;

    private final int generatorUsageCount;

    private final GeneratorChoiceStrategyType generatorChoiceStrategyType;

    private GeneratorChoiceContext(Builder builder) {
        this.random = builder.random;
        this.generators = builder.generators;
        this.generatorUsageCount = builder.generatorUsageCount;
        this.generatorChoiceStrategyType = builder.generatorChoiceStrategyType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Random getRandom() {
        return random;
    }

    public List<TimeSeriesGenerator> getGenerators() {
        return generators;
    }

    public int getGeneratorUsageCount() {
        return generatorUsageCount;
    }

    public GeneratorChoiceStrategyType getGeneratorChoiceStrategyType() {
        return generatorChoiceStrategyType;
    }

    public static final class Builder {

        private Random random;

        private List<TimeSeriesGenerator> generators;

        private int generatorUsageCount;

        private GeneratorChoiceStrategyType generatorChoiceStrategyType;

        public GeneratorChoiceContext build() {
            return new GeneratorChoiceContext(this);
        }

        public Builder setRandom(Random random) {
            this.random = random;
            return this;
        }

        public Builder setGenerators(List<TimeSeriesGenerator> generators) {
            this.generators = generators;
            return this;
        }

        public Builder setGeneratorUsageCount(int generatorUsageCount) {
            this.generatorUsageCount = generatorUsageCount;
            return this;
        }

        public Builder setGeneratorChoiceStrategyType(GeneratorChoiceStrategyType generatorChoiceStrategyType) {
            this.generatorChoiceStrategyType = generatorChoiceStrategyType;
            return this;
        }
    }
}
