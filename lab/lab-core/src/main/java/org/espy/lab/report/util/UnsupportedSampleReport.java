package org.espy.lab.report.util;

import org.espy.lab.report.TimeSeriesProcessorReport;
import org.espy.lab.sample.TimeSeriesSample;

import java.io.PrintWriter;

public class UnsupportedSampleReport implements TimeSeriesProcessorReport {

    private final String message;

    public UnsupportedSampleReport(String processorName,
                                   TimeSeriesSample sample) {
        this.message = processorName
                + " doesn't support: "
                + sample.getMetadata();
    }

    public UnsupportedSampleReport(String processorName,
                                   TimeSeriesSample sample,
                                   Exception exception) {
        this.message = processorName
                + " was failed on sample: "
                + sample.getMetadata()
                + ", with exception: "
                + exception.getMessage();
    }

    @Override public void write(PrintWriter writer) {
        writer.write(message);
    }
}
