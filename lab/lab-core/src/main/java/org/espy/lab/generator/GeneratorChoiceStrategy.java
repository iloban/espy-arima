package org.espy.lab.generator;

public interface GeneratorChoiceStrategy {

    boolean hasNext();

    TimeSeriesGenerator next();
}
