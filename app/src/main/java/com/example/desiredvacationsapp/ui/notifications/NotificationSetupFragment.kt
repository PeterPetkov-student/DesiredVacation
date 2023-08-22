package com.example.desiredvacationsapp.ui.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.desiredvacationsapp.R
import com.example.desiredvacationsapp.viewmodel.NotificationSetupViewModel
import java.text.SimpleDateFormat
import java.util.*

class NotificationSetupFragment : Fragment() {

    private lateinit var viewModel: NotificationSetupViewModel

    private lateinit var dateButton: TextView
    private lateinit var timeButton: TextView
    private var vacationId: Int = 0

    // Constants
    companion object {
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

        viewModel = ViewModelProvider(this)[NotificationSetupViewModel::class.java]

        vacationId = arguments?.getInt("vacation_id") ?: 0
        val vacationName = arguments?.getString("vacation_name") ?: ""
        setupViews(view, vacationName)

        // Observe the calendar live data
        viewModel.calendar.observe(viewLifecycleOwner) { calendar ->
            val dateSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateButton.text = dateSdf.format(calendar.time)
            val timeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeButton.text = timeSdf.format(calendar.time)
        }
    }

    private fun setupViews(view: View, vacationName: String) {
        val notificationMessageInput = view.findViewById<EditText>(R.id.notificationMessageInput)
        dateButton = view.findViewById(R.id.dateButton)
        timeButton = view.findViewById(R.id.timeButton)
        val setNotificationButton = view.findViewById<Button>(R.id.setNotificationButton)

        dateButton.setOnClickListener {
            handleDateButtonClick()
        }

        timeButton.setOnClickListener {
            handleTimeButtonClick()
        }

        setNotificationButton.setOnClickListener {
            handleSetNotificationButtonClick(notificationMessageInput,vacationName)
        }
    }

    private fun handleDateButtonClick() {
        val calendar = viewModel.calendar.value ?: Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(Calendar.YEAR, selectedYear)
            calendar.set(Calendar.MONTH, selectedMonth)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            viewModel.updateCalendar(calendar)
        }, year, month, day).show()
    }

    private fun handleTimeButtonClick() {
        val calendar = viewModel.calendar.value ?: Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            viewModel.updateCalendar(calendar)
        }, hour, minute, true).show()
    }

    private fun handleSetNotificationButtonClick(notificationMessageInput: EditText, vacationName: String) {
        val message = notificationMessageInput.text.toString()
        val notificationId = vacationId
        createNotificationChannel()
        viewModel.updateNotificationMessage(message)
        scheduleNotification(notificationId, vacationName, message, viewModel.calendar.value!!)
    }
    private fun scheduleNotification(notificationId: Int, title: String, message: String, calendar: Calendar) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        intent.putExtra("notification_id", notificationId)
        intent.putExtra("title", title)
        intent.putExtra("message", message)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        parentFragmentManager.popBackStack()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Scheduled Notifications"
            val descriptionText = "Channel for scheduled notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}