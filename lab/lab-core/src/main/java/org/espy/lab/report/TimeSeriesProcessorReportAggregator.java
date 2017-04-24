package org.espy.lab.report;

import java.util.List;

public interface TimeSeriesProcessorReportAggregator<R extends TimeSeriesProcessorReport> {

    AggregatedTimeSeriesProcessorReport aggregate(List<R> reports);
}
