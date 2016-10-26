package org.espy.lab;

import java.io.PrintWriter;

public interface TimeSeriesProcessor {

    void marshal(PrintWriter writer);

    ProcessorReport run(TimeSeriesSample sample);
}
