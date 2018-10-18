package com.yiqizuoye.library.notificationcompatcolor;


import android.app.Activity;
import android.app.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

    private final int mSmallIconId = R.mipmap.action;
    private final int mLargeIconId = R.mipmap.notifi_icon;
    private TextView mTestInfoTextView;
    private String channelID = "1";
    private String channelName = "channel_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTestInfoTextView = (TextView) findViewById(R.id.testinfo_text);
    }

    public void onClickSystemNotification(View v) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this);
            builder.setTicker("System Ticker O")
                    .setContentTitle("System ContentTitle O")
                    .setContentText("System ContentText O")
                    .setSmallIcon(mSmallIconId)
                    .setLargeIcon(getLargeIcon())
                    .setChannelId(channelID);

            manager.notify(0, builder.build());
        } else {

            Notification.Builder builder = new Notification.Builder(this);
            builder.setTicker("System Ticker")
                    .setContentTitle("System ContentTitle")
                    .setContentText("System ContentText")
                    .setSmallIcon(mSmallIconId)
                    .setLargeIcon(getLargeIcon());

            getNotificationManager().notify(0, builder.getNotification());

        }

    }

    private Bitmap getLargeIcon() {
        return BitmapFactory.decodeResource(getResources(), mLargeIconId);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void onClickCustomNotification(View v) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this);
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout_withbutton);

            builder.setCustomContentView(remoteViews);

            NotificationCompatColor.AutomationUse(this)
                    .setContentTitleColor(remoteViews, R.id.notification_content_title)
                    .setContentTextColor(remoteViews, R.id.notification_content_text);
            remoteViews.setTextViewText(R.id.notification_content_title, "Custom ContentTitle O");
            remoteViews.setTextViewText(R.id.notification_content_text, "Custom ContentText O");
            remoteViews.setImageViewResource(R.id.notification_large_icon, mLargeIconId);

            builder.setTicker("Custom Ticker O")
                    .setCustomContentView(remoteViews)
                    .setSmallIcon(R.mipmap.action)
                    .setAutoCancel(true)
                    .setChannelId(channelID);
            Notification notification = builder.build();

            manager.notify(1, notification);

        } else {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setTicker("Custom Ticker")
                    .setSmallIcon(mSmallIconId)
                    .setLargeIcon(getLargeIcon());
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout_withbutton);

            builder.setContent(remoteViews);

            NotificationCompatColor.AutomationUse(this)
                    .setContentTitleColor(remoteViews, R.id.notification_content_title)
                    .setContentTextColor(remoteViews, R.id.notification_content_text);
            remoteViews.setTextViewText(R.id.notification_content_title, "Custom ContentTitle");
            remoteViews.setTextViewText(R.id.notification_content_text, "Custom ContentText");
            remoteViews.setImageViewResource(R.id.notification_large_icon, mLargeIconId);
            getNotificationManager().notify(1, builder.getNotification());


        }


    }

    public void onClickByAuto(View v) {
        NotificationCompatColor fetcher = new NotificationCompatColor(this).byAutomation();
        showTestInfo(fetcher.toString());
    }

    private void showTestInfo(String text) {
        mTestInfoTextView.setText(text);
    }

    public void onClickByText(View v) {
        NotificationCompatColor fetcher = new NotificationCompatColor(this).byText();
        showTestInfo(fetcher.toString());
    }

    public void onClickById(View v) {
        NotificationCompatColor fetcher = new NotificationCompatColor(this).byId();
        showTestInfo(fetcher.toString());
    }

    public void onClickByAnyText(View v) {
        NotificationCompatColor fetcher = new NotificationCompatColor(this).byAnyTextView();
        showTestInfo(fetcher.toString());
    }

    public void onClickBySdkVersion(View v) {
        NotificationCompatColor fetcher = new NotificationCompatColor(this).bySdkVersion();
        showTestInfo(fetcher.toString());
    }

}

