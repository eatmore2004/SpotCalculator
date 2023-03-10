package com.andrii.positioncalculator.Helpers;

import static java.lang.Math.abs;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnalyzeAPI {
    private final String url;
    private final int[] price_triggers = {1000,500,200,100,50,0};
    public AnalyzeAPI(String url) {
        this.url = url;
    }

    public int[] check() throws IOException {
        Response response = getResponse();
        if(response != null){
            assert response.body() != null;
            String responseData = response.body().string();
            int trigger = getTrigger();
            return checkCrossTrigger(trigger, responseData);
        }else return new int[]{4, 0, 0};
    }

    private static int[] checkCrossTrigger(int trigger_price, String response){
        double[] prices = parseResponse(response);
        double delta_price = abs(prices[1] - prices[2]);
        int[] result = {0,0,0};
        if (delta_price * 1.02 >= trigger_price){
            result[0] = (prices[0] > prices[3]) ? -1 : 1;
            result[1] = (int) prices[0];
            result[2] = (int) prices[3];
        }
        return result;
    }

    private static double[] parseResponse(String response) {

        Gson gson = new Gson();

        Object[][] dataArray = gson.fromJson(response, Object[][].class);

        double[] openPrices = new double[dataArray.length];
        double[] highPrices = new double[dataArray.length];
        double[] lowPrices = new double[dataArray.length];
        double[] closePrices = new double[dataArray.length];

        for (int i = 0; i < dataArray.length; i++) {
            Object[] kline = dataArray[i];
            openPrices[i] = Double.parseDouble((String) kline[1]);
            highPrices[i] = Double.parseDouble((String) kline[2]);
            lowPrices[i] = Double.parseDouble((String) kline[3]);
            closePrices[i] = Double.parseDouble((String) kline[4]);
        }

        double[] prices = new double[4];

        prices[0] = openPrices[0];                       // Open price
        prices[1] = getMaxPrice(highPrices);             // High price
        prices[2] = getLowPrice(lowPrices);              // Low price
        prices[3] = closePrices[closePrices.length - 1]; // Close price

        return prices;
    }

    private int getTrigger() {
        int price_var = StorageManager.getIntVariable("Price");
        return price_triggers[price_var];
    }

    private static double getLowPrice(double[] lowPrices) {
        double minLow = Double.MAX_VALUE;
        for (double lowPrice : lowPrices) {
            if (lowPrice < minLow) {
                minLow = lowPrice;
            }
        }
        return minLow;
    }

    private static double getMaxPrice(double[] highPrices) {
        double maxHigh = Double.MIN_VALUE;
        for (double highPrice : highPrices) {
            if (highPrice > maxHigh) {
                maxHigh = highPrice;
            }
        }
        return maxHigh;
    }

    private Response getResponse() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
