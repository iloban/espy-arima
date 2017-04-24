package org.espy.lab;

public interface GeneratorChoiceStrategy {

    boolean hasNext();

    TimeSeriesGenerator next();
}
