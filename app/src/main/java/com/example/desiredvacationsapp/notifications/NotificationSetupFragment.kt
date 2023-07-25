package com.example.desiredvacationsapp.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.desiredvacationsapp.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NotificationSetupFragment : Fragment() {

    // Constants
    companion object {
        // Channel ID for Android 8.0 and above. Used to associate this notification channel
        // with notifications posted to it.
        const val CHANNEL_ID = "ScheduledNotificationsChannel"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view) // Set up the views for this fragment
    }

    // Find and setup the necessary views
    private fun setupViews(view: View) {
        // Find the views
        val notificationMessageInput = view.findViewById<EditText>(R.id.notificationMessageInput)
        val dateInput = view.findViewById<EditText>(R.id.dateInput)
        val timeInput = view.findViewById<EditText>(R.id.timeInput)
        val setNotificationButton = view.findViewById<Button>(R.id.setNotificationButton)

        // Set a click listener for the "Set Notification" button
        setNotificationButton.setOnClickListener {
            handleSetNotificationButtonClick(notificationMessageInput, dateInput, timeInput)
        }
    }

    // Handles the logic when the "Set Notification" button is clicked
    private fun handleSetNotificationButtonClick(
        notificationMessageInput: EditText,
        dateInput: EditText,
        timeInput: EditText
    ) {
        // Get the user input for notification message, date, and time
        val message = notificationMessageInput.text.toString()
        val date = dateInput.text.toString()
        val time = timeInput.text.toString()

        val notificationId = 1

        // Create a notification channel for Android 8.0 and above.
        // Notification channels are only available in these versions.
        createNotificationChannel()

        // Schedule the notification
        scheduleNotification(notificationId, message, date, time)
    }

    // This method schedules the notification for the provided date and time
    private fun scheduleNotification(notificationId: Int, message: String, date: String, time: String) {
        // Create a Calendar instance to hold the date and time when the notification should be triggered
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        try {
            // Parse the user input date and time
            val dateTime = sdf.parse("$date $time")
            if (dateTime != null) {
                calendar.time = dateTime
            }
        } catch (e: ParseException) {
            // Handle parsing error
            return
        }

        // Create an alarm to trigger the notification
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("message", message) // Add the notification message to the intent
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Set the alarm to trigger at the exact date and time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        // Pop this fragment from the stack
        parentFragmentManager.popBackStack()
    }

    // This method creates a notification channel for Android 8.0 and above
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = "Scheduled Notifications"
            val descriptionText = "Channel for scheduled notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}