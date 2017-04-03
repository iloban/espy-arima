package org.espy.lab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TimeSeriesSuiteConfiguration {

    private final int seed;

    private final List<TimeSeriesGenerator> generators;

    private final GeneratorChoiceStrategyType generatorChoiceStrategyType;

    private final int generatorUsageCount;

    private TimeSeriesSuiteConfiguration(Builder builder) {
        this.seed = builder.seed;
        this.generators = builder.generators;
        this.generatorChoiceStrategyType = builder.generatorChoiceStrategyType;
        this.generatorUsageCount = builder.generatorUsageCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getSeed() {
        return seed;
    }

    public List<TimeSeriesGenerator> getGenerators() {
        return generators;
    }

    public GeneratorChoiceStrategyType getGeneratorChoiceStrategyType() {
        return generatorChoiceStrategyType;
    }

    public int getGeneratorUsageCount() {
        return generatorUsageCount;
    }

    public static final class Builder {

        private int seed;

        private List<TimeSeriesGenerator> generators;

        private GeneratorChoiceStrategyType generatorChoiceStrategyType;

        private int generatorUsageCount;

        public TimeSeriesSuiteConfiguration build() {
            return new TimeSeriesSuiteConfiguration(this);
        }

        public Builder setSeed(int seed) {
            this.seed = seed;
            return this;
        }

        public Builder setGenerators(List<TimeSeriesGenerator> generators) {
            this.generators = Collections.unmodifiableList(new ArrayList<>(generators));
            return this;
        }

        public Builder setChoiceStrategy(GeneratorChoiceStrategyType generatorChoiceStrategyType) {
            this.generatorChoiceStrategyType = generatorChoiceStrategyType;
            return this;
        }

        public Builder setGeneratorUsageCount(int generatorUsageCount) {
            this.generatorUsageCount = generatorUsageCount;
            return this;
        }
    }
}
