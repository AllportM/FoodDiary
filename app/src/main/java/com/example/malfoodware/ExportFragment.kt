package com.example.malfoodware

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.diary_entry_create_finalize.*
import kotlinx.android.synthetic.main.export_data_fragment.*
import java.util.*

class ExportFragment:
    Fragment(),
    CalendarUseIF
{
    enum class Type
    {
        FROM, TO
    }

    companion object
    {
        val FRAGMENT_TAG = "exportDataFrag"
        var CALENDAR = Calendar.getInstance()
        val BAR_TITLE = "Create Diary Entry"
    }

    interface ExportActivityListener
    {
        fun onLaunchCalendarFromExport(timeMilli: Long, frag: ExportFragment)
        fun onExportData(from: String, to: String)
    }

    init {
        CALENDAR = Calendar.getInstance()
    }

    lateinit var activityApp: ExportActivityListener
    lateinit var type: Type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.export_data_fragment, container, false)
        return v
    }

    // init variables
    // initilaizes ViewTexts and sets the text values for given entry
    override fun onStart() {
        super.onStart()
    }

    // init listener iinterface to app
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ExportActivityListener)
            activityApp = context
        else
            Log.d("INITLOG", "Error attaching listener in ${this::class}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LOG", "${this::class} view created")
        setDateTimeListeners()
        setButtons()
    }

    private fun setDateTimeListeners()
    {
        Log.d("LOG", "${this::class} setting date and time events")
        exportDateFrom.setOnClickListener {
            type = Type.FROM
            activityApp.onLaunchCalendarFromExport(CALENDAR.timeInMillis, this)
        }
        exportDateTo.setOnClickListener {
            type = Type.TO
            activityApp.onLaunchCalendarFromExport(CALENDAR.timeInMillis, this)
        }
    }

    private fun setButtons()
    {
        exportBack.setOnClickListener {
            activity?.onBackPressed()
        }
        Log.d("LOG", "${this::class} setting finish button events")
        exportFinalize.setOnClickListener {
            clearError()
            if (checkDates())
            {
                val dateFromText = exportDateFrom.text.toString()
                val dateToText = exportDateTo.text.toString()
                activityApp.onExportData(dateFromText, dateToText)
            }
        }
    }

    private fun checkDates(): Boolean
    {
        var dateFromText = exportDateFrom.text.toString()
        if (dateFromText.equals(""))
            dateFromText = "0/0/0"
        var dateToText = exportDateTo.text.toString()
        if (dateToText.equals(""))
            dateToText = "31/12/5000"
        if (dateFromText > dateToText)
        {
            exportDateError.visibility = TextView.VISIBLE
            return false
        }
        return true
    }

    private fun clearError()
    {
        exportDateError.visibility = TextView.INVISIBLE
    }

    fun setDate(date: String)
    {
        when (type)
        {
            Type.FROM -> exportDateFrom.setText(date)
            Type.TO -> exportDateTo.setText(date)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOG", "")
    }

    override fun onDateSet(year: Int, month: Int, day: Int) {
        Log.d("LOG", "${this::class.java} recieved response from calendar, setting date")
        val dateString = "$day/${month+1}/$year"
        setDate(dateString)
    }
}