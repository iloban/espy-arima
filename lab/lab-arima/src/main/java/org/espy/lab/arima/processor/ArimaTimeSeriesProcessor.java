package org.espy.lab.arima.processor;

import org.espy.arima.ArimaProcess;
import org.espy.lab.arima.fitter.ArimaFitter;
import org.espy.lab.arima.forecast.ArimaForecaster;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;
import org.espy.lab.processor.TimeSeriesProcessor;
import org.espy.lab.report.TimeSeriesProcessorReport;
import org.espy.lab.report.TimeSeriesProcessorReportProducer;
import org.espy.lab.sample.TimeSeriesSample;

import java.io.PrintWriter;

public final class ArimaTimeSeriesProcessor<R extends TimeSeriesProcessorReport> implements TimeSeriesProcessor<R> {

    private final ArimaFitter fitter;

    private final ArimaForecaster forecaster;

    private final TimeSeriesProcessorReportProducer<R> reportProducer;

    public ArimaTimeSeriesProcessor(ArimaFitter fitter,
                                    ArimaForecaster forecaster,
                                    TimeSeriesProcessorReportProducer<R> reportProducer) {
        this.fitter = fitter;
        this.forecaster = forecaster;
        this.reportProducer = reportProducer;
    }

    @Override public boolean support(TimeSeriesSample sample) {
        return sample.getMetadata() instanceof ArimaTimeSeriesSampleMetadata;
    }

    @Override public void init() {
        ArimaTimeSeriesSampleMetadata.register();
    }

    @Override public R process(TimeSeriesSample sample) {
        ArimaTimeSeriesSampleMetadata metadata = (ArimaTimeSeriesSampleMetadata) sample.getMetadata();
        double[] observedPart = sample.getObservedPart();
        double[] unobservedPart = sample.getUnobservedPart();
        ArimaProcess arimaProcess = fitter.fit(metadata, observedPart);
        double[] forecast = forecaster.forecast(arimaProcess, observedPart, unobservedPart.length);
        return reportProducer.produce(metadata, unobservedPart, forecast);
    }

    @Override public void write(PrintWriter writer) {
        writer.println(getName());
        writer.print("    Fitter : ");
        fitter.write(writer);
        writer.println();
        writer.print("Forecaster : ");
        forecaster.write(writer);
        writer.println();
        writer.print("Comparator : ");
        reportProducer.write(writer);
    }

    @Override public String getName() {
        return "ARIMA processor";
    }
}
