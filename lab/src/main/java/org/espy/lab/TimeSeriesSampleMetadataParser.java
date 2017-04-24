package org.espy.lab;

import java.util.Scanner;

public class TimeSeriesSampleMetadataParser {

    public static TimeSeriesSampleMetadata read(Scanner scanner) {
        TimeSeriesSampleType<? extends TimeSeriesSampleMetadata> type = TimeSeriesSampleType.valueOf(scanner.next());
        return type.reader().apply(scanner);
    }
}
