package org.espy.lab;

import java.util.Scanner;

import static org.espy.lab.TimeSeriesSampleMetadataType.get;

public class TimeSeriesSampleMetadataParser {

    public static TimeSeriesSampleMetadata read(Scanner scanner) {
        TimeSeriesSampleMetadataType<? extends TimeSeriesSampleMetadata> metadataType = get(scanner.next());
        return metadataType.read(scanner);
    }
}
