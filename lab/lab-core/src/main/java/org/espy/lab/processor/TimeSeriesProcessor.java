package org.espy.lab.processor;

import org.espy.lab.report.TimeSeriesProcessorReport;
import org.espy.lab.sample.TimeSeriesSample;
import org.espy.lab.util.Writable;

public interface TimeSeriesProcessor<R extends TimeSeriesProcessorReport> extends Writable {

    String getName();

    void init();

    boolean support(TimeSeriesSample sample);

    R process(TimeSeriesSample sample);
}
