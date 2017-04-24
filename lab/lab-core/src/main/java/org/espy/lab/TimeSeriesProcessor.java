package org.espy.lab;

public interface TimeSeriesProcessor extends Writable {

    void init();

    TimeSeriesProcessorReport run(TimeSeriesSample sample);
}
