package org.espy.lab;

import java.util.List;
import java.util.Random;

public class RandomChoiceStrategy implements ChoiceStrategy {

    @Override public TimeSeriesGenerator select(ChoiceContext context) {
        List<TimeSeriesGenerator> generators = context.getGenerators();
        Random random = context.getRandom();
        return generators.get(random.nextInt(generators.size()));
    }
}
