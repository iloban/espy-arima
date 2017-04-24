package org.espy.lab.generator;

import org.espy.lab.sample.TimeSeriesSample;

public interface TimeSeriesGenerator {

    TimeSeriesSample generate(GeneratorContext generatorContext);
}
