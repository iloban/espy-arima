package org.espy.lab.sample.metadata;

import java.util.Scanner;

public class TimeSeriesSampleMetadataParser {

    public static TimeSeriesSampleMetadata read(Scanner scanner) {
        TimeSeriesSampleMetadataType<? extends TimeSeriesSampleMetadata> metadataType = TimeSeriesSampleMetadataType.get(scanner.next());
        return metadataType.read(scanner);
    }
}
