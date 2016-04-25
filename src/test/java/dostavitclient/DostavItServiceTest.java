package dostavitclient;

import dostavitclient.data.CarrierInfo;
import dostavitclient.data.Rate;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class DostavItServiceTest {

    @Test
    public void getCarriers() throws Exception {
        List<CarrierInfo> carrierInfoList = DostavItService.getCarriers();
        Assert.assertNotNull(carrierInfoList);
        Assert.assertFalse(carrierInfoList.isEmpty());
    }

    @Test
    public void getCarrier() throws Exception {
        CarrierInfo carrier = DostavItService.getCarrier("DelLin");
        Assert.assertNotNull(carrier);
        Assert.assertTrue(carrier.getName() != null && !carrier.getName().isEmpty());
        Assert.assertTrue(carrier.getFullName() != null && !carrier.getFullName().isEmpty());
        Assert.assertTrue(carrier.getUrl() != null && !carrier.getUrl().isEmpty());
        Assert.assertTrue(carrier.getLargeLogoUrl() != null && !carrier.getLargeLogoUrl().isEmpty());
        Assert.assertNotNull(carrier.getServices());
        Assert.assertFalse(carrier.getServices().isEmpty());
    }

    @Test
    public void getRates() throws Exception {
        List<Rate> rates = DostavItService.getRates("Москва", "Саратов", 100, 80, 50, 15, BigDecimal.valueOf(1500));
        Assert.assertNotNull(rates);
        Assert.assertFalse(rates.isEmpty());
    }

    @Test
    public void getCarrierRates() throws Exception {
        List<Rate> rates = DostavItService.getRates("DelLin", "Москва", "Саратов", 100, 80, 50, 15, BigDecimal.valueOf(1500));
        Assert.assertNotNull(rates);
        Assert.assertFalse(rates.isEmpty());
        for (Rate rate : rates) {
            Assert.assertEquals("dellin", rate.getCarrier().toLowerCase());
        }
    }
}

