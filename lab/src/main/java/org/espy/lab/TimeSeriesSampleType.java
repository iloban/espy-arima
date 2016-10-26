package org.espy.lab;

import java.util.Scanner;
import java.util.function.Function;

public enum TimeSeriesSampleType {

    ARIMA(ArimaTimeSeriesSampleMetadata::unmarshal);

    private final Function<Scanner, TimeSeriesSampleMetadata> unmarshaller;

    TimeSeriesSampleType(Function<Scanner, TimeSeriesSampleMetadata> unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public Function<Scanner, TimeSeriesSampleMetadata> unmarshaller() {
        return unmarshaller;
    }
}
