package com.andrii.positioncalculator.Services;


import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.andrii.positioncalculator.Helpers.StorageManager;
import com.andrii.positioncalculator.SettingsActivity;
import com.example.positioncalculator.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIFetchService extends Service {

    private final Timer timer = new Timer();

    private final TimerTask task = new TimerTask() {
        @SuppressLint({"SetTextI18n", "MissingPermission"})
        @Override
        public void run() {
            if (!(isSleepMode() && isNowSleepTime())){
                if (isInternetConnected(APIFetchService.this)){
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://api.binance.com/api/v3/klines?symbol=BTCUSDT&interval=1m&limit=15")
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        assert response.body() != null;
                        String responseData = response.body().string();
                        int TRIGGER = 200;
                        int[] result_code = checkCrossTrigger(TRIGGER, responseData);
                        if (result_code[0] != 0) {

                            int icon = (result_code[0] == -1)? R.drawable.short_direction : R.drawable.long_direction;

                            NotificationCompat.Builder builderWear = new NotificationCompat.Builder(APIFetchService.this, "BinanceServiceChannel")
                                    .setContentTitle((result_code[0] == 1)? "Вверх" : "Вниз")
                                    .setContentText(result_code[1] + " -> " + result_code[2])
                                    .setSmallIcon(icon)
                                    .setVibrate(new long[]{0, 200, 100, 200, 100, 200, 100, 200});

                            NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
                            wearableExtender.setHintShowBackgroundOnly(true);
                            builderWear.extend(wearableExtender);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(APIFetchService.this);
                            notificationManager.notify(0, builderWear.build());
                        }
                        String lastUpdate = "Последнее обновление:\n" + getDate();
                        StorageManager.saveVariable("Update",lastUpdate);
                        SettingsActivity.text.setText(lastUpdate);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    };

    private String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd.MM.yy HH:mm", new Locale("ru"));
        String formattedDate = formatter.format(new Date());
        formattedDate = Character.toUpperCase(formattedDate.charAt(0)) + formattedDate.substring(1);
        return formattedDate;
    }

    private boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (networkInfos != null) {
                for (NetworkInfo networkInfo : networkInfos) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isNowSleepTime() {
        LocalTime now = LocalTime.now();
        LocalTime sleepStart = LocalTime.of(23, 0);
        LocalTime sleepEnd = LocalTime.of(6, 30);

        return now.isAfter(sleepStart) || now.isBefore(sleepEnd);
    }

    private boolean isSleepMode() {
        return StorageManager.getBoolVariable("Sleep_mode");
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        int PERIOD = 15 * 60 * 1000;
        timer.schedule(task, 0, PERIOD);
        return START_STICKY;
    }

    private void startForegroundService() {
        NotificationChannel channel = new NotificationChannel(
                "BinanceServiceChannel",
                "Binance Service Channel",
                NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, "BinanceServiceChannel")
                .setContentTitle("Calculator")
                .setContentText("Все хорошо. Наблюдаем за курсом...")
                .setSmallIcon(R.drawable.icon)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
