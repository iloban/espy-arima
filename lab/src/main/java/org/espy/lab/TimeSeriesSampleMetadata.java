package org.espy.lab;

import java.io.PrintWriter;

public interface TimeSeriesSampleMetadata {

    TimeSeriesSampleType getType();

    void marshal(PrintWriter writer);
}
