package com.sample.clinic.Services;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.sample.clinic.MainActivity;
import com.sample.clinic.R;

public class ReminderNotif extends BroadcastReceiver {
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_TEXT = "notification_text";

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {


        String channelId = "myNotificationChannel";
        NotificationChannel notificationChannel = new NotificationChannel(channelId, "Notify", NotificationManager.IMPORTANCE_HIGH);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        String notificationText = intent.getStringExtra(NOTIFICATION_TEXT);

        if (notificationId < 0) {
            int id = notificationId * -1;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.putExtra("isNotif", true);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                    context, 0, intent1,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.cliniclogo)
                    .setContentTitle("Scheduled Notification")
                    .setContentText(notificationText)
                    .setSound(defaultSoundUri);
            notification.setContentIntent(notifyPendingIntent);
            notificationManager.notify(notificationId, notification.build());
        }


    }
}
