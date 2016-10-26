package org.espy.lab;

import java.io.File;

public class ExperimentFactory {

    public static Experiment createExperiment(ExperimentConfiguration configuration) {
        File file = new File(configuration.getTimeSeriesSuiteFileName());
        if (!file.exists()) {
            throw new IllegalArgumentException("A time series suite file is not found");
        }
        return new Experiment(file.getAbsolutePath(), configuration.getProcessors());
    }
}
