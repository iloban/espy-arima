package org.espy.lab;

public interface TimeSeriesProcessor extends Writable {

    TimeSeriesProcessorReport run(TimeSeriesSample sample);
}
