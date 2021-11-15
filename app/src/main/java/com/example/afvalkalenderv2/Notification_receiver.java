package com.example.afvalkalenderv2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.core.app.NotificationCompat;


public class Notification_receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context,Repeating_activity.class);
        //if needed replace previous activity
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String summary = intent.getStringExtra("Summary");

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My notifications");
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setContentTitle("Afval kalender");
        builder.setContentText(summary);
        builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        builder.setAutoCancel(true);

        if (intent.getAction().equals("MY_NOTIFICATION_MESSAGE")) {
            notificationManager.notify(100, builder.build());
            Log.i("Notify", "Alarm");

        }

    }


}
