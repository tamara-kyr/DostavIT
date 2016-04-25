package dostavitclient.data;

import java.util.Date;

public class Rate {
    private  String carrier;
    private  String service;
    private Date shipDate;
    private Date deliveryDate;
    private Money cost;
    private Money insurance;

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Money getCost() {
        return cost;
    }

    public void setCost(Money cost) {
        this.cost = cost;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Money getInsurance() {
        return insurance;
    }

    public void setInsurance(Money insurance) {
        this.insurance = insurance;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }
}
