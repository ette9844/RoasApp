package com.demo.project.roas.demo2;

/**
 * Created by is on 2017-11-26.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

//푸시 메시지가 들어왔을떄 실제 사용자에게 푸시 알람을 만들어서 띄워주는 클래스
//api를 통해 푸시 알림을 전송하면 입력한 내용이 message에 담겨서 오게됨
public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    //메시지 수신시 할것
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
// TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendPushNotification(remoteMessage.getData().get("message"));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendPushNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void sendPushNotification(String message) {
    // [START receive_message]
        System.out.println("received message : " + message);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.roasicon144).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher) )
                .setContentTitle("음식완료!")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setLights(000000255,500,2000)
                .setContentIntent(pendingIntent);

        Log.d("message", message);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
