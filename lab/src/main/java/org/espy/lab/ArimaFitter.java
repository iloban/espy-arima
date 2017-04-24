package org.espy.lab;

import org.espy.arima.ArimaProcess;

public interface ArimaFitter extends Writable {

    ArimaProcess fit(ArimaTimeSeriesSampleMetadata metadata, double[] values);
}
