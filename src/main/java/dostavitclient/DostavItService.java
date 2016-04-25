package dostavitclient;

import dostavitclient.data.CarrierInfo;
import dostavitclient.data.DeliveryService;
import dostavitclient.data.Money;
import dostavitclient.data.Rate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DostavItService {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATETIME_FORMAT_WITH_TIME_ZONE = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final int TIMEOUT_VALUE = 5 * 60000; // // 5 minutes connect  and read timeout

    public static List<CarrierInfo> getCarriers() throws DostavItServiceException {
        try {
            String response = sendGetRequest("http://dostav.it/api/carriers");

            List<CarrierInfo> carrierList = new ArrayList<CarrierInfo>();
            if (response == null || !response.isEmpty()) {
                carrierList = parseListOfCarriers(response);
            }
            return carrierList;
        } catch (Exception e) {
            throw createDostavItServiceException("Error getting carriers", e);
        }
    }

    public static CarrierInfo getCarrier(String carrierName) throws DostavItServiceException {
        validateNotNull(carrierName, "Carrier name should not be null");
        try {
            String response = sendGetRequest(String.format("http://dostav.it/api/carriers/%s", carrierName));
            CarrierInfo carrierInfo = null;
            if (response == null || !response.isEmpty()) {
                carrierInfo = createCarrierInfo(new JSONObject(response));
            }
            return carrierInfo;
        } catch (Exception e) {
            throw createDostavItServiceException("Error getting carrier: " + carrierName, e);
        }
    }

    public static List<Rate> getRates(String fromCity, String toCity, double lengthCm, double widthCm, double heightCm, double weightKg, BigDecimal costRub, Date shipDate) throws DostavItServiceException {
        return getRates("", fromCity, toCity, lengthCm, widthCm, heightCm, weightKg, costRub, shipDate);
    }

    public static List<Rate> getRates(String fromCity, String toCity, double lengthCm, double widthCm, double heightCm, double weightKg, BigDecimal costRub) throws DostavItServiceException {
        return getRates(fromCity, toCity, lengthCm, widthCm, heightCm, weightKg, costRub, null);
    }

    public static List<Rate> getRates(String carrier, String fromCity, String toCity, double lengthCm, double widthCm, double heightCm, double weightKg, BigDecimal costRub, Date shipDate) throws DostavItServiceException {
        validateNotNull(carrier, "Carrier  should not be null");
        validateNotNull(fromCity, "From city  should not be null");
        validateNotNull(toCity, "To city  should not be null");
        validateNotNull(lengthCm, "Length  should not be null");
        validateNotNull(widthCm, "Width  should not be null");
        validateNotNull(heightCm, "Height  should not be null");
        validateNotNull(weightKg, "Weight should not be null");
        validateNotNull(costRub, "Cost should not be null");
        try {
            String request = getJsonString(fromCity, toCity, lengthCm, widthCm, heightCm, weightKg, costRub, shipDate);
            String response = sendPostRequest(String.format("http://dostav.it/api/rates/%s", carrier), request.toString());
            List<Rate> rateList = new ArrayList<Rate>();
            if (response == null || !response.isEmpty()) {
                rateList = parseListOfRates(response);
            }
            return rateList;
        } catch (Exception e) {
            throw createDostavItServiceException("Error getting rates for carrier: " + carrier, e);
        }
    }

    public static List<Rate> getRates(String carrier, String fromCity, String toCity, double lengthCm, double widthCm, double heightCm, double weightKg, BigDecimal costRub) throws DostavItServiceException {
        return getRates(carrier, fromCity, toCity, lengthCm, widthCm, heightCm, weightKg, costRub, null);
    }

    private static String sendPostRequest(String urlString, String requestContent) throws Exception {
        String response = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(TIMEOUT_VALUE);
            conn.setReadTimeout(TIMEOUT_VALUE);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("Accept", "application/json;charset=utf-8");

            // Send post request
            conn.setDoOutput(true);
            BufferedWriter out =
                    new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            out.write(requestContent);
            out.flush();
            out.close();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
            }
            response = readResponse(conn);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }

    private static String sendGetRequest(String urlString) throws Exception {
        String response = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(TIMEOUT_VALUE);
            conn.setReadTimeout(TIMEOUT_VALUE);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("Accept", "application/json;charset=utf-8");
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
            }
            response = readResponse(conn);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuffer responseStringBuffer = new StringBuffer();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                responseStringBuffer.append(inputLine);
            }
            String response = responseStringBuffer.toString();
            return response;
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    private static List<CarrierInfo> parseListOfCarriers(String response) {
        List<CarrierInfo> carrierList = new ArrayList<CarrierInfo>();
        JSONArray carriers = new JSONArray(response);
        if (carriers != null && carriers.length() != 0) {
            for (int i = 0; i < carriers.length(); i++) {
                JSONObject carrier = carriers.getJSONObject(i);
                carrierList.add(createCarrierInfo(carrier));
            }
        }
        return carrierList;
    }

    private static CarrierInfo createCarrierInfo(JSONObject carrier) {
        CarrierInfo carrierInfo = new CarrierInfo();
        carrierInfo.setFullName(carrier.getString("fullName"));
        carrierInfo.setName(carrier.getString("name"));
        carrierInfo.setUrl(carrier.getString("url"));
        carrierInfo.setLargeLogoUrl(carrier.getString("largeLogoUrl"));
        JSONArray services = carrier.getJSONArray("services");
        if (services != null && services.length() != 0) {
            carrierInfo.setServices(new ArrayList<DeliveryService>());
            for (int j = 0; j < services.length(); j++) {
                JSONObject service = services.getJSONObject(j);
                carrierInfo.getServices().add(createDeliveryService(service));
            }
        }
        return carrierInfo;
    }

    private static DeliveryService createDeliveryService(JSONObject service) {
        DeliveryService deliveryService = new DeliveryService();
        deliveryService.setName(service.getString("name"));
        deliveryService.setFullName(service.getString("fullName"));
        return deliveryService;
    }

    private static String getJsonString(String fromCity, String toCity, double lengthCm, double widthCm, double heightCm, double weightKg, BigDecimal costRub, Date shipDate) throws Exception {
        JSONObject request = new JSONObject();
        JSONObject parcel = new JSONObject();

        JSONObject cost = new JSONObject();
        cost.put("amount", costRub);
        cost.put("currency", "RUB");
        JSONObject length = new JSONObject();
        length.put("value", lengthCm);
        length.put("units", "Cm");
        JSONObject width = new JSONObject();
        width.put("value", widthCm);
        width.put("units", "Cm");
        JSONObject height = new JSONObject();
        height.put("value", heightCm);
        height.put("units", "Cm");
        JSONObject weight = new JSONObject();
        weight.put("value", weightKg);
        weight.put("units", "Kg");

        parcel.put("cost", cost);
        parcel.put("length", length);
        parcel.put("width", width);
        parcel.put("height", height);
        parcel.put("weight", weight);

        JSONObject from = new JSONObject();
        from.put("country", "RU");
        from.put("city", fromCity);

        JSONObject to = new JSONObject();
        to.put("country", "RU");
        to.put("city", toCity);

        request.put("parcel", parcel);
        request.put("from", from);
        request.put("to", to);
        request.put("shipDate", shipDate);

        return request.toString();
    }

    private static List<Rate> parseListOfRates(String response) throws Exception {
        List<Rate> rateList = new ArrayList<Rate>();
        JSONArray rates = new JSONArray(response);
        if (rates != null && rates.length() != 0) {
            for (int i = 0; i < rates.length(); i++) {
                JSONObject rateJson = rates.getJSONObject(i);
                rateList.add(createRate(rateJson));
            }
        }
        return rateList;
    }

    private static Rate createRate(JSONObject rateJson) throws Exception {
        Rate rate = new Rate();
        rate.setCarrier(rateJson.optString("carrier", null));
        rate.setService(rateJson.optString("service", null));
        String shipDateString = rateJson.optString("shipDate", null);
        rate.setShipDate(parse(shipDateString));
        String deliveryDateString = rateJson.optString("deliveryDate", null);
        rate.setDeliveryDate(parse(deliveryDateString));

        JSONObject costJson = rateJson.getJSONObject("cost");
        rate.setCost(createMoney(costJson));
        JSONObject insuranceJson = rateJson.getJSONObject("insurance");
        rate.setInsurance(createMoney(insuranceJson));
        return rate;
    }

    private static Money createMoney(JSONObject moneyJson) throws Exception {
        Money money = new Money();
        money.setAmount(moneyJson.getBigDecimal("amount"));
        money.setCurrency(moneyJson.getString("currency"));
        return money;
    }

    public static Date parse(String input) throws java.text.ParseException {
        Date date = null;
        if (input == null || input.isEmpty()) {
            return date;
        }

        try {
            date = new SimpleDateFormat(DATETIME_FORMAT_WITH_TIME_ZONE).parse(input);
        } catch (ParseException e) {
            date = new SimpleDateFormat(DATETIME_FORMAT).parse(input);
        }

        return date;
    }

    private static DostavItServiceException createDostavItServiceException(String msg, Exception e) {
        return new DostavItServiceException(msg, e);
    }

    private static DostavItServiceException createDostavItServiceException(String msg) {
        return new DostavItServiceException(msg);
    }

    private static void validateNotNull(Object obj, String msg)  throws  DostavItServiceException {
        if (obj == null) {
            throw createDostavItServiceException(msg);
        }
    }
}
