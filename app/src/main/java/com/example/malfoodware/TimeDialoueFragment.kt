package com.example.malfoodware

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.*
import kotlin.math.min

class TimeDialoueFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    companion object
    {
        val FRAGMENT_TAG = "timeDialogueFrag"
    }

    interface TimeDialogueInterface
    {
        fun onTimeClicked(hourOfDay: Int, minute: Int)
    }

    private lateinit var activityApp: TimeDialogueInterface

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = CreateEntryFinalFragment.CALENDAR
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, android.R.style.Theme_Material_Light_Dialog_Alert,this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TimeDialogueInterface)
        {
            activityApp = context
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        activityApp.onTimeClicked(hourOfDay, minute)
    }
}