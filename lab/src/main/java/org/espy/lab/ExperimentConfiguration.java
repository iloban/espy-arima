package org.espy.lab;

import java.util.Arrays;
import java.util.List;

public final class ExperimentConfiguration {

    private final String timeSeriesSuiteFileName;

    private final List<TimeSeriesProcessor> processors;

    private ExperimentConfiguration(Builder builder) {
        this.timeSeriesSuiteFileName = builder.timeSeriesSuiteFileName;
        this.processors = builder.processors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTimeSeriesSuiteFileName() {
        return timeSeriesSuiteFileName;
    }

    public List<TimeSeriesProcessor> getProcessors() {
        return processors;
    }

    public static final class Builder {

        private String timeSeriesSuiteFileName;

        private List<TimeSeriesProcessor> processors;

        public ExperimentConfiguration build() {
            return new ExperimentConfiguration(this);
        }

        public Builder setTimeSeriesSuiteFileName(String timeSeriesSuiteFileName) {
            this.timeSeriesSuiteFileName = timeSeriesSuiteFileName;
            return this;
        }

        public Builder setProcessors(TimeSeriesProcessor... processors) {
            this.processors = Arrays.asList(processors);
            return this;
        }
    }
}
