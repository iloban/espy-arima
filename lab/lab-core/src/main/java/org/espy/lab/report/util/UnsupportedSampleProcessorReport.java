package org.espy.lab.report.util;

import org.espy.lab.processor.TimeSeriesProcessor;
import org.espy.lab.report.TimeSeriesProcessorReport;
import org.espy.lab.sample.TimeSeriesSample;

import java.io.PrintWriter;

public class UnsupportedSampleProcessorReport implements TimeSeriesProcessorReport {

    private final String message;

    public UnsupportedSampleProcessorReport(TimeSeriesProcessor<?> processor,
                                            TimeSeriesSample sample) {
        this.message = processor.getName()
                + " doesn't support: "
                + sample.getMetadata();
    }

    public UnsupportedSampleProcessorReport(TimeSeriesProcessor<?> processor,
                                            TimeSeriesSample sample,
                                            Exception exception) {
        this.message = processor.getName()
                + " was failed on sample: "
                + sample.getMetadata()
                + ", with exception: "
                + exception.getMessage();
    }

    @Override public void write(PrintWriter writer) {
        writer.write(message);
    }
}
