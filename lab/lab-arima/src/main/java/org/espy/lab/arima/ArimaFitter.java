package org.espy.lab.arima;

import org.espy.arima.ArimaProcess;
import org.espy.lab.Writable;

public interface ArimaFitter extends Writable {

    ArimaProcess fit(ArimaTimeSeriesSampleMetadata metadata, double[] values);
}
