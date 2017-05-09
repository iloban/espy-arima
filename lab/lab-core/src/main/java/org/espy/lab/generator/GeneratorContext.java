package org.espy.lab.generator;

import java.util.Random;

public final class GeneratorContext {

    private final Random random;

    private final int observedPartLength;

    private final int unobservedPartLength;

    private GeneratorContext(Builder builder) {
        this.random = builder.random;
        this.observedPartLength = builder.observedPartLength;
        this.unobservedPartLength = builder.unobservedPartLength;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Random getRandom() {
        return random;
    }

    public int getObservedPartLength() {
        return observedPartLength;
    }

    public int getUnobservedPartLength() {
        return unobservedPartLength;
    }

    public static final class Builder {

        private Random random;

        private int observedPartLength = 40;

        private int unobservedPartLength = 10;

        public GeneratorContext build() {
            return new GeneratorContext(this);
        }

        public Builder setRandom(Random random) {
            this.random = random;
            return this;
        }

        public Builder setObservedPartLength(int observedPartLength) {
            this.observedPartLength = observedPartLength;
            return this;
        }

        public Builder setUnobservedPartLength(int unobservedPartLength) {
            this.unobservedPartLength = unobservedPartLength;
            return this;
        }
    }
}
