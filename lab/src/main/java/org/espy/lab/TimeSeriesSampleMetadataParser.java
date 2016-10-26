package org.espy.lab;

import java.util.Scanner;

public class TimeSeriesSampleMetadataParser {

    public static TimeSeriesSampleMetadata unmarshal(Scanner scanner) {
        TimeSeriesSampleType type = TimeSeriesSampleType.valueOf(scanner.next());
        return type.unmarshaller().apply(scanner);
    }
}
