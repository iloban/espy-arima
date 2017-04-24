package org.espy.lab.report;

import java.io.PrintWriter;

public abstract class AbstractTimeSeriesProcessorReport implements TimeSeriesProcessorReport {

    private final boolean error;

    protected AbstractTimeSeriesProcessorReport(boolean error) {
        this.error = error;
    }

    @Override public final void write(PrintWriter writer) {
        if (error) {
            writeErrorReport(writer);
        } else {
            writeNormalReport(writer);
        }
    }

    protected abstract void writeNormalReport(PrintWriter writer);

    protected abstract void writeErrorReport(PrintWriter writer);
}
