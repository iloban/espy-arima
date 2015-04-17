package org.espy.arima;

public interface ArimaModel {
    int getArOrder();

    int getIntegratedOrder();

    int getMaOrder();
}
