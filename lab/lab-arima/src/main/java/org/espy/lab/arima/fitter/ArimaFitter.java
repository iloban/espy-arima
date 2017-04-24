package org.espy.lab.arima.fitter;

import org.espy.arima.ArimaProcess;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;
import org.espy.lab.util.Writable;

public interface ArimaFitter extends Writable {

    ArimaProcess fit(ArimaTimeSeriesSampleMetadata metadata, double[] values);
}
