package com.example.malfoodware

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatDialogFragment
import java.util.*


class CalendarViewFragment(val time: Long, val intF: CalendarUseIF) : AppCompatDialogFragment() {

    lateinit var calendar: Calendar
    lateinit var activityApp: DatePickerListener

    companion object
    {
        val CALENDAR_FRAGMENT_TAG = "calendarViewTag"
    }

    interface DatePickerListener
    {
        fun calDateClickedEntry(date: String)
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
        super.dismiss()
//        activityApp.onCalDismiss()
    }

    fun setCalendar()
    {
        calendar = Calendar.getInstance()
        val calendarView = requireView().findViewById<CalendarView>(R.id.my_date_picker)
        calendarView.date = time
        calendarView?.setOnDateChangeListener { calendarView, i, i2, i3 ->
            Log.d("LOG", "${this::class.java} calendar set, sending response to ${intF::class.java}")
            intF.onDateSet(i, i2, i3)
            if (intF is CreateEntryFinalFragment)
                super.dismiss()
            else
                dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCalendar()
    }
}