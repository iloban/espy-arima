package org.espy.lab;

public interface ChoiceStrategy {

    TimeSeriesGenerator select(ChoiceContext context);
}
