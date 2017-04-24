package org.espy.lab.sample.metadata;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class TimeSeriesSampleMetadataType<T extends TimeSeriesSampleMetadata> {

    private static Map<String, TimeSeriesSampleMetadataType<? extends TimeSeriesSampleMetadata>> registeredTypes
            = new ConcurrentHashMap<>();

    private final String name;

    private final Function<Scanner, T> reader;

    private TimeSeriesSampleMetadataType(String name, Function<Scanner, T> reader) {
        this.name = name;
        this.reader = reader;
    }

    public static void register(String name, Function<Scanner, ? extends TimeSeriesSampleMetadata> reader) {
        registeredTypes.computeIfAbsent(name, key -> new TimeSeriesSampleMetadataType<>(key, reader));
    }

    public static TimeSeriesSampleMetadataType<? extends TimeSeriesSampleMetadata> get(String value) {
        TimeSeriesSampleMetadataType<? extends TimeSeriesSampleMetadata> type = registeredTypes.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unregistered metadata type: " + value);
        }
        return type;
    }

    public T read(Scanner scanner) {
        return reader.apply(scanner);
    }

    @Override public String toString() {
        return name;
    }
}
