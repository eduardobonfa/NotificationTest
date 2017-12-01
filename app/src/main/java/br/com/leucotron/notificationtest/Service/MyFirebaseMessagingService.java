package br.com.leucotron.notificationtest.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.leucotron.notificationtest.R;

/**
 * Created by eduardoguimaraes on 01/12/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    public static final String ANDROID_CHANNEL_ID = "com.leucotron.uc.android";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
    private NotificationManager mNotificationManager;
    private final int NOTIFICATION_CHAT_ID = 1002;
    private long[] vibrationPattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        androidChannel.enableLights(true);
        androidChannel.setVibrationPattern(null);
        androidChannel.setSound(null, null);
        androidChannel.setLightColor(R.color.colorAccent);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getNotificationManager().createNotificationChannel(androidChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), ANDROID_CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_launcher_background);
        // builder.setContentTitle(...)
        builder.setContentText(remoteMessage.getNotification().getBody());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // play vibration
            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1));

            // play sound
            Intent serviceIntent = new Intent(getApplicationContext(), SoundService.class);
            serviceIntent.setAction("ACTION_START_PLAYBACK");
            serviceIntent.putExtra("SOUND_URI", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            getApplicationContext().startForegroundService(serviceIntent);

            // the delete intent will stop the sound when the notification is cleared
            Intent deleteIntent = new Intent(getApplicationContext(), SoundService.class);
            deleteIntent.setAction("ACTION_STOP_PLAYBACK");
            PendingIntent pendingDeleteIntent =
                    PendingIntent.getService(getApplicationContext(), 0, deleteIntent, 0);
            builder.setDeleteIntent(pendingDeleteIntent);

        } else {

            builder.setVibrate(vibrationPattern);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        }

        mNotificationManager.notify(NOTIFICATION_CHAT_ID, builder.build());
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }
}