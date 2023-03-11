package com.andrii.positioncalculator.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.andrii.positioncalculator.Activities.SettingsActivity;
import com.andrii.positioncalculator.Helpers.AnalyzeAPI;
import com.andrii.positioncalculator.Helpers.StorageManager;
import com.example.positioncalculator.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TickerService extends Service {

    private final Timer timer = new Timer();
    private final int[] periods = {30,15,5,1};

    private final TimerTask task = new TimerTask() {

        @Override
        public void run() {
            if (!(isSleepMode() && isNowSleepTime())){
                if (isInternetConnected(TickerService.this)){
                    AnalyzeAPI analyzeAPI = new AnalyzeAPI("https://api.binance.com/api/v3/klines?symbol=BTCUSDT&interval=1m&limit=15");
                    int[] result_code;
                    try {
                        result_code = analyzeAPI.check();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (result_code[0] != 0) {

                        int icon = getIcon(result_code[0]);
                        String title = getTitle(result_code[0]);
                        String text = getText(result_code[1],result_code[2]);
                        sendNotification(text,title,icon);
                    }
                    actualizeDate();
                }
            }

        }

    };

    private void sendNotification(String text, String title, int icon) {


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel("BinanceServiceChannel", "Binance Service Channel", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(TickerService.this, "BinanceServiceChannel")
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icon);
        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .setBigContentTitle(title)
                .setSummaryText(text);
        builder.setStyle(style);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        notificationManager.notify(0, notification);
    }


    private String getText(int last, int now) {
        return last + " -> " + now;
    }

    private String getTitle(int code) {
        return (code == 1)? "Вверх" : "Вниз";
    }

    private int getIcon(int code) {
        return (code == -1)? R.drawable.short_direction : R.drawable.long_direction;
    }

    private void actualizeDate() {
        String lastUpdate = "Последнее обновление:\n" + getDate();
        StorageManager.saveVariable("Update",lastUpdate);
        SettingsActivity.text.setText(lastUpdate);
    }


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
        LocalTime sleepStart = LocalTime.of(23, 59);
        LocalTime sleepEnd = LocalTime.of(6, 30);

        return now.isAfter(sleepStart) || now.isBefore(sleepEnd);
    }

    private boolean isSleepMode() {
        return StorageManager.getBoolVariable("Sleep_mode");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        int period = getPeriod();
        timer.schedule(task, 0, period);
        return START_STICKY;
    }

    private int getPeriod() {
        int period_var = StorageManager.getIntVariable("Period");
        return periods[period_var] * 60 * 1000;
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
