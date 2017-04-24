package org.espy.lab.sample.metadata;

import org.espy.lab.util.Writable;

public interface TimeSeriesSampleMetadata extends Writable {

    int getObservedPartLength();

    int getUnobservedPartLength();
}
