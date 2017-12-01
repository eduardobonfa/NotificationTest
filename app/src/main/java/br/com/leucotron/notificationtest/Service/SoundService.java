package br.com.leucotron.notificationtest.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import br.com.leucotron.notificationtest.R;

/**
 * Created by eduardoguimaraes on 01/12/2017.
 */

public class SoundService extends Service {

    MediaPlayer mMediaPlayer;
    public static final String ANDROID_CHANNEL_ID = "com.leucotron.uc.android";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL2";
    private NotificationManager mNotificationManager;
    private final int NOTIFICATION_CHAT_ID = 1002;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationChannel androidChannel2 = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
        androidChannel2.enableLights(true);
        androidChannel2.setVibrationPattern(null);
        androidChannel2.setSound(null, null);
        androidChannel2.setLightColor(R.color.colorAccent);
        androidChannel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getNotificationManager().createNotificationChannel(androidChannel2);

        // foreground notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, ANDROID_CHANNEL_ID);

            startForeground(NOTIFICATION_CHAT_ID, builder.build());
        }

        // check action
        String action = intent.getAction();
        switch (action) {
            case "ACTION_START_PLAYBACK":
                startSound(intent.getStringExtra("RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))"));
                break;
            case "ACTION_STOP_PLAYBACK":
                stopSound();
                break;
        }

        // service will not be recreated if abnormally terminated
        return START_NOT_STICKY;
    }

    private void startSound(String uriString) {

        // parse sound
        Uri soundUri;
        try {
            soundUri = Uri.parse(uriString);
        } catch (Exception e) {
            cleanup();
            return;
        }

        // play sound
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    cleanup();
                }
            });
        }
        try {
            mMediaPlayer.setDataSource(this, soundUri);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            cleanup();
        }

    }

    private void stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        cleanup();
    }

    private void cleanup() {
        stopSelf();
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

}
