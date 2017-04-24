package org.espy.lab;

public interface TimeSeriesSampleMetadata extends Writable {

    int getObservedPartLength();

    int getUnobservedPartLength();
}
