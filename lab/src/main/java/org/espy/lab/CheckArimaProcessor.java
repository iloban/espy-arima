package org.espy.lab;

import org.espy.arima.ArimaForecaster;
import org.espy.arima.DefaultArimaForecaster;
import org.espy.arima.DefaultArimaProcess;
import org.espy.arima.ForecastAccuracyRelativeMetric;

import java.io.PrintWriter;
import java.util.Arrays;

public class CheckArimaProcessor implements TimeSeriesProcessor {

    @Override public void marshal(PrintWriter writer) {
    }

    @Override public ProcessorReport run(TimeSeriesSample sample) {
        TimeSeriesSampleMetadata metadata = sample.getMetadata();
        if (metadata.getType() != TimeSeriesSampleType.ARIMA) {
            return new ProcessorReport(metadata);
        }
        ArimaTimeSeriesSampleMetadata arimaMetadata = (ArimaTimeSeriesSampleMetadata) metadata;
        DefaultArimaProcess process = new DefaultArimaProcess();
        process.setArCoefficients(arimaMetadata.getArCoefficients());
        process.setIntegrationOrder(arimaMetadata.getIntegrationOrder());
        process.setMaCoefficients(arimaMetadata.getMaCoefficients());
        double[] checkValues = sample.getValues();
        double[] checkTail = Arrays.copyOfRange(checkValues, 10, checkValues.length);
        double[] forecast = new double[checkTail.length];
        for (int i = 0; i < checkTail.length; i++) {
            double[] checkHead = Arrays.copyOfRange(checkValues, i, i + 9);
            ArimaForecaster forecaster = new DefaultArimaForecaster(process, checkHead);
            forecast[i] = forecaster.next();
        }
        double farm = ForecastAccuracyRelativeMetric.getValue(checkTail, forecast);
        return new ProcessorReport(metadata, farm);
    }
}
