package org.espy.lab;

import java.util.ArrayList;
import java.util.List;

public class ArimaGenerators {

    public static List<TimeSeriesGenerator> natural(int observedPartLength, int unobservedPartLength) {
        List<TimeSeriesGenerator> generators = new ArrayList<>();
        for (int d = 0; d <= 2; d++) {
            for (int q = 0; q <= 2; q++) {
                for (int p = 0; p <= 2; p++) {
                    if (p != 0 && q != 0) {
                        generators.add(new ArimaGenerator(p, d, q, observedPartLength, unobservedPartLength));
                    }
                }
            }
        }
        return generators;
    }
}
