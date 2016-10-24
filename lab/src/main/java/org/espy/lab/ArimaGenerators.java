package org.espy.lab;

public class ArimaGenerators {

    public static TimeSeriesGenerator[] natural() {
        return new TimeSeriesGenerator[]{
                new ArimaGenerator(1, 0, 0),
                new ArimaGenerator(2, 0, 0),
                new ArimaGenerator(0, 0, 1),
                new ArimaGenerator(0, 0, 2),
                new ArimaGenerator(1, 0, 1),
                new ArimaGenerator(2, 0, 2),
                new ArimaGenerator(1, 1, 0),
                new ArimaGenerator(2, 1, 0),
                new ArimaGenerator(0, 1, 1),
                new ArimaGenerator(0, 1, 2),
                new ArimaGenerator(1, 1, 1),
                new ArimaGenerator(2, 1, 2),
                new ArimaGenerator(1, 2, 0),
                new ArimaGenerator(2, 2, 0),
                new ArimaGenerator(0, 2, 1),
                new ArimaGenerator(0, 2, 2),
                new ArimaGenerator(1, 2, 1),
                new ArimaGenerator(2, 2, 2)
        };
    }
}
