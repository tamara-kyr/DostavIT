package dostavitclient.data;

import java.util.List;

public class CarrierInfo {
    private String name;
    private String fullName;
    private String url;
    private String largeLogoUrl;
    public List<DeliveryService> services;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLargeLogoUrl() {
        return largeLogoUrl;
    }

    public void setLargeLogoUrl(String largeLogoUrl) {
        this.largeLogoUrl = largeLogoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DeliveryService> getServices() {
        return services;
    }

    public void setServices(List<DeliveryService> services) {
        this.services = services;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
