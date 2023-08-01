package com.example.desiredvacationsapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*


class NotificationSetupViewModel(application: Application) : AndroidViewModel(application) {
    private val _calendar: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance())
    val calendar: LiveData<Calendar> = _calendar

    private val _notificationMessage: MutableLiveData<String> = MutableLiveData("")

    // Functions to update the mutable live data

    fun updateCalendar(calendar: Calendar) {
        _calendar.value = calendar
    }

    fun updateNotificationMessage(message: String) {
        _notificationMessage.value = message
    }
}
