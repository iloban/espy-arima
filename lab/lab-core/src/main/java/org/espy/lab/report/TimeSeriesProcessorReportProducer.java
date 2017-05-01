package org.espy.lab.report;

import org.espy.lab.sample.TimeSeriesSample;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;
import org.espy.lab.util.Writable;

public interface TimeSeriesProcessorReportProducer<R extends TimeSeriesProcessorReport> extends Writable {

    R produce(TimeSeriesSampleMetadata metadata, TimeSeriesSample observations, double[] forecast);
}
