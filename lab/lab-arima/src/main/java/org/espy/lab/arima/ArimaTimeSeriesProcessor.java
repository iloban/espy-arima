package org.espy.lab.arima;

import org.espy.arima.ArimaProcess;
import org.espy.lab.*;

import java.io.PrintWriter;

public final class ArimaTimeSeriesProcessor implements TimeSeriesProcessor {

    private static final String PROCESSOR_NAME = "ARIMA processor";

    private final ArimaFitter fitter;

    private final ArimaForecaster forecaster;

    private final ForecastComparator forecastComparator;

    public ArimaTimeSeriesProcessor(ArimaFitter fitter,
                                    ArimaForecaster forecaster,
                                    ForecastComparator forecastComparator) {
        this.fitter = fitter;
        this.forecaster = forecaster;
        this.forecastComparator = forecastComparator;
    }

    @Override public void init() {
        ArimaTimeSeriesSampleMetadata.register();
    }

    @Override public TimeSeriesProcessorReport run(TimeSeriesSample sample) {
        TimeSeriesSampleMetadata metadata = sample.getMetadata();
        if (!(metadata instanceof ArimaTimeSeriesSampleMetadata)) {
            return new UnsupportedSampleProcessorReport(PROCESSOR_NAME, metadata);
        }
        ArimaTimeSeriesSampleMetadata arimaMetadata = ArimaTimeSeriesSampleMetadata.class.cast(metadata);
        double[] observedPart = sample.getObservedPart();
        double[] unobservedPart = sample.getUnobservedPart();
        ArimaProcess arimaProcess = fitter.fit(arimaMetadata, observedPart);
        double[] forecast = forecaster.forecast(arimaProcess, observedPart, unobservedPart.length);
        return forecastComparator.compare(metadata, unobservedPart, forecast);
    }

    @Override public void write(PrintWriter writer) {
        writer.println(PROCESSOR_NAME);
        writer.print("    Fitter : ");
        fitter.write(writer);
        writer.println();
        writer.print("Forecaster : ");
        forecaster.write(writer);
        writer.println();
        writer.print("Comparator : ");
        forecastComparator.write(writer);
    }
}
