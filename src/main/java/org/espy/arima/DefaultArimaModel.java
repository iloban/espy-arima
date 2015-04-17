package org.espy.arima;

public class DefaultArimaModel implements ArimaModel {
    private int arOrder;
    private int integratedOrder;
    private int maOrder;

    @Override
    public int getArOrder() {
        return arOrder;
    }

    public void setArOrder(int arOrder) {
        this.arOrder = arOrder;
    }

    @Override
    public int getIntegratedOrder() {
        return integratedOrder;
    }

    public void setIntegratedOrder(int integratedOrder) {
        this.integratedOrder = integratedOrder;
    }

    @Override
    public int getMaOrder() {
        return maOrder;
    }

    public void setMaOrder(int maOrder) {
        this.maOrder = maOrder;
    }
}
