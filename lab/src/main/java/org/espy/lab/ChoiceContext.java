package org.espy.lab;

import java.util.List;
import java.util.Random;

public final class ChoiceContext {

    private final List<TimeSeriesGenerator> generators;

    private final Random random;

    private ChoiceContext(Builder builder) {
        this.generators = builder.generators;
        this.random = builder.random;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<TimeSeriesGenerator> getGenerators() {
        return generators;
    }

    public Random getRandom() {
        return random;
    }

    public static final class Builder {

        private List<TimeSeriesGenerator> generators;

        private Random random;

        public ChoiceContext build() {
            return new ChoiceContext(this);
        }

        public Builder setGenerators(List<TimeSeriesGenerator> generators) {
            this.generators = generators;
            return this;
        }

        public Builder setRandom(Random random) {
            this.random = random;
            return this;
        }
    }
}
