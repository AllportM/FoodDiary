package com.example.malfoodware

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.*


class CalendarViewFragment() : AppCompatDialogFragment() {
    lateinit var calendar: Calendar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.calendar_view_fragment, container, false)

        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return v
    }

    fun setCalendar()
    {
        calendar = Calendar.getInstance()
        val calendarView = requireView().findViewById<CalendarView>(R.id.my_date_picker)
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
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val dateString = "$day/$month/$year"
        // send date back to the target fragment
        targetFragment!!.onActivityResult(
            targetRequestCode,
            Activity.RESULT_OK,
            Intent().putExtra("selectedDate", dateString)
        )
    }
}