package com.udacity.sanketbhat.news4you.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.model.Article;

import java.util.List;

public class NewArticleNotification {

    private static final String NOTIFICATION_TAG = "NewArticle";
    private static final String NOTIFICATION_CHANNEL_ID = "newArticlesChannelId";
    private static final String NOTIFICATION_CHANNEL_NAME = "Article Updates";
    private static final int NOTIFICATION_ID = 12321;

    private static void createChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.notification_channel_description));
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void notify(final Context context,
                              List<Article> newArticles, final int number) {
        createChannel(context);

        final String notificationTitle = context.getString(R.string.new_article_notification_title_template);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < newArticles.size(); i++) {
            stringBuilder.append(newArticles.get(i).getTitle()).append("\n");
        }

        String subTitle = stringBuilder.toString();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

                // Set defaults for the notification light, sound, and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification notificationTitle, and text.
                .setSmallIcon(R.drawable.ic_stat_new_article)
                .setContentTitle(notificationTitle)
                .setContentText(subTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Set ticker text (preview) information for this notification.
                .setTicker(context.getString(R.string.notification_title))

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Show expanded text content on devices running Android 4.1 or
                // later.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(subTitle)
                        .setBigContentTitle(notificationTitle)
                        .setSummaryText(subTitle))

                .setGroup(NOTIFICATION_TAG)

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());
    }

}
