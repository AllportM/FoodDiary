package com.example.malfoodware

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.*


class CalendarViewFragment(val type: Int, val time: Long) : AppCompatDialogFragment() {

    lateinit var calendar: Calendar
    lateinit var activityApp: DatePickerListener

    init {
        LAST_TYPE = type
    }

    constructor() : this(LAST_TYPE, TODAY)

    companion object
    {
        val CALENDAR_FRAGMENT_TAG = "calendarViewTag"
        var LAST_TYPE = MainActivity.CALENDAR_TYPE_ENTRY
        val TODAY = Calendar.getInstance().timeInMillis
    }

    interface DatePickerListener
    {
        fun calDateClickedEntry(date: String)
        fun calDateClickedFrom(date: String)
        fun calDateClickedTo(date: String)
        fun onCalDismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.calendar_view_fragment, container, false)

        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DatePickerListener)
            activityApp = context
        else
            Log.d("INITLOG", "Error attaching listener in LoginFragment, context must implement onLogin interface")
    }

    override fun onDismiss(dialog: DialogInterface) {
        activityApp.onCalDismiss()
    }

    fun setCalendar()
    {
        calendar = Calendar.getInstance()
        val calendarView = requireView().findViewById<CalendarView>(R.id.my_date_picker)
        calendarView.date = time
        calendarView?.setOnDateChangeListener { calendarView, i, i2, i3 ->
            onDateSet(i, i2, i3)
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCalendar()
    }

    override fun onResume() {
        super.onResume()
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        val dateString = "$day/${month+1}/$year"
        // send date back to the target fragment
        when(type)
        {
            MainActivity.CALENDAR_TYPE_ENTRY -> activityApp.calDateClickedEntry(dateString)
            MainActivity.CALENDAR_TYPE_FROM -> activityApp.calDateClickedFrom(dateString)
            MainActivity.CALENDAR_TYPE_TO -> activityApp.calDateClickedTo(dateString)
        }
    }
}