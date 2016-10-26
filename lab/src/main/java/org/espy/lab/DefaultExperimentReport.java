package org.espy.lab;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

public class DefaultExperimentReport implements ExperimentReport {

    private final List<ProcessorReport> reports;

    public DefaultExperimentReport(List<ProcessorReport> reports) {
        this.reports = reports;
    }

    @Override public void marshal(PrintWriter writer) {
        Iterator<ProcessorReport> iterator = reports.iterator();
        while (iterator.hasNext()) {
            ProcessorReport report = iterator.next();
            report.marshal(writer);
            if (iterator.hasNext()) {
                writer.print("\n\n");
            }
        }
    }
}
