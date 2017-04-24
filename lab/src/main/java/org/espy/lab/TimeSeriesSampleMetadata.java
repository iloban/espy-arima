package org.espy.lab;

public interface TimeSeriesSampleMetadata extends Writable {

    TimeSeriesSampleType getType();

    int getObservedPartLength();

    int getUnobservedPartLength();
}
