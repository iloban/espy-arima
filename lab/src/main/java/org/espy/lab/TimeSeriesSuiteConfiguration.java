package org.espy.lab;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class TimeSeriesSuiteConfiguration {

    private final int seed;

    private final List<TimeSeriesGenerator> generators;

    private final ChoiceStrategyType choiceStrategyType;

    private final int generatorUsageCount;

    private TimeSeriesSuiteConfiguration(Builder builder) {
        this.seed = builder.seed;
        this.generators = builder.generators;
        this.choiceStrategyType = builder.choiceStrategyType;
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

    public ChoiceStrategyType getChoiceStrategyType() {
        return choiceStrategyType;
    }

    public int getGeneratorUsageCount() {
        return generatorUsageCount;
    }

    public static final class Builder {

        private int seed;

        private List<TimeSeriesGenerator> generators;

        private ChoiceStrategyType choiceStrategyType;

        private int generatorUsageCount;

        public TimeSeriesSuiteConfiguration build() {
            return new TimeSeriesSuiteConfiguration(this);
        }

        public Builder setSeed(int seed) {
            this.seed = seed;
            return this;
        }

        public Builder setGenerators(TimeSeriesGenerator[] generators) {
            this.generators = ImmutableList.copyOf(generators);
            return this;
        }

        public Builder setChoiceStrategyType(ChoiceStrategyType choiceStrategyType) {
            this.choiceStrategyType = choiceStrategyType;
            return this;
        }

        public Builder setGeneratorUsageCount(int generatorUsageCount) {
            this.generatorUsageCount = generatorUsageCount;
            return this;
        }
    }
}
