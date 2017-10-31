package com.example.ljuni.lapitchat.Chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.ljuni.lapitchat.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ljuni on 30/10/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_msg = remoteMessage.getNotification().getBody();

        String from_user_id = remoteMessage.getData().get("from_user_id");

        String click_action = remoteMessage.getNotification().getClickAction();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icone)
                .setContentTitle("New Friend Request")
                .setContentText("You have received a new Friend Request");

        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("user_id", from_user_id);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity
                        (this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationID = (int) System.currentTimeMillis();

        NotificationManager mNotifyMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMng.notify(mNotificationID, mBuilder.build());
    }
}
