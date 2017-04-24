package org.espy.lab;

import java.util.List;

public interface TimeSeriesProcessorReportAggregator {

    AggregatedTimeSeriesProcessorReport aggregate(List<TimeSeriesProcessorReport> reports);
}
