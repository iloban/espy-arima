package org.espy.lab;

import java.util.Random;

public final class GeneratorContext {

    private final Random random;

    private GeneratorContext(Builder builder) {
        this.random = builder.random;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Random getRandom() {
        return random;
    }

    public static final class Builder {

        private Random random;

        public GeneratorContext build() {
            return new GeneratorContext(this);
        }

        public Builder setRandom(Random random) {
            this.random = random;
            return this;
        }
    }
}
