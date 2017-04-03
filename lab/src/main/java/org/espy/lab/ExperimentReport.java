package org.espy.lab;

import org.espy.arima.ForecastAccuracyRelativeMetric;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

public class ExperimentReport {

    private final List<ProcessorReport> reports;

    public ExperimentReport(List<ProcessorReport> reports) {
        this.reports = reports;
    }

    public void marshal(PrintWriter writer) {
        double[] farmValues = reports.stream().mapToDouble(ProcessorReport::getFarm).toArray();
        double totalFarm = ForecastAccuracyRelativeMetric.combine(farmValues);
        writer.printf("Total FARM: %f%%\n\n", totalFarm * 100);
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
