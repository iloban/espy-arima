package org.espy.arima;

public interface ArimaModel {
    int getArOrder();

    int getIntegrationOrder();

    int getMaOrder();
}
