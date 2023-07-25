package com.example.desiredvacationsapp.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    // Here it's being used to receive the broadcast from an alarm triggering.
    @SuppressLint("PrivateResource")
    override fun onReceive(context: Context, intent: Intent) {

        // The ID for the notification, it can be used later to update or cancel the notification
        val notificationId = 1

        // Build the notification with its parameters:
        // The icon, the title, the text, and the priority.
        val notificationBuilder = NotificationCompat.Builder(context, NotificationSetupFragment.CHANNEL_ID)
            .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp) // set the icon for the notification
            .setContentTitle("Scheduled Notification") // set the title of the notification
            .setContentText(intent.getStringExtra("message")) // set the text of the notification with the extra "message" in the intent
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // set the priority of the notification

        // The NotificationManagerCompat is used to notify the user of events that happen in the background.
        // It is used here to display the notification we just built.
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}