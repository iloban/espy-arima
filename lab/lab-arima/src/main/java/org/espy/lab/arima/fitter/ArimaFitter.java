package org.espy.lab.arima.fitter;

import org.espy.arima.ArimaProcess;
import org.espy.lab.sample.metadata.TimeSeriesSampleMetadata;
import org.espy.lab.util.Writable;

public interface ArimaFitter extends Writable {

    boolean support(TimeSeriesSampleMetadata metadata);

    ArimaProcess fit(TimeSeriesSampleMetadata metadata, double[] observations);
}
