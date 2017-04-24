package org.espy.lab;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public final class TimeSeriesSampleType<T extends TimeSeriesSampleMetadata> {

    public static final TimeSeriesSampleType<ArimaTimeSeriesSampleMetadata> ARIMA = new TimeSeriesSampleType<>(
            "ARIMA",
            ArimaTimeSeriesSampleMetadata.class,
            ArimaTimeSeriesSampleMetadata::read
    );

    private static Map<String, TimeSeriesSampleType<? extends TimeSeriesSampleMetadata>> values;

    private final String name;

    private final Class<T> metadataClass;

    private final Function<Scanner, T> reader;

    private TimeSeriesSampleType(String name, Class<T> metadataClass, Function<Scanner, T> reader) {
        this.name = name;
        this.metadataClass = metadataClass;
        this.reader = reader;
        if (values == null) {
            values = new LinkedHashMap<>();
        }
        values.put(name, this);
    }

    public static TimeSeriesSampleType<? extends TimeSeriesSampleMetadata> valueOf(String value) {
        TimeSeriesSampleType<? extends TimeSeriesSampleMetadata> type = values.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unexpected sample type: " + value);
        }
        return type;
    }

    public Function<Scanner, T> reader() {
        return reader;
    }

    public boolean supports(TimeSeriesSampleMetadata metadata) {
        return metadataClass.isAssignableFrom(metadata.getClass());
    }

    public T cast(TimeSeriesSampleMetadata metadata) {
        return metadataClass.cast(metadata);
    }

    @Override public String toString() {
        return name;
    }
}
