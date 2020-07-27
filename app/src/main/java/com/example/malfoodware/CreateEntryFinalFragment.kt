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
import java.util.*

class CreateEntryFinalFragment: Fragment() {

    companion object
    {
        val FRAGMENT_TAG = "createEntryFinalFrag"
        var CALENDAR = Calendar.getInstance()
    }

    interface CreateEntryFinalListener
    {
        fun onDateClick()
        fun onTimeClick()
        fun onFinishEntry(entry: FoodDiaryEntry): Boolean
        fun onFinish()
    }

    init {
        CALENDAR = Calendar.getInstance()
    }

    lateinit var view1: View
    lateinit var activityApp: CreateEntryFinalListener
    var errorTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.diary_entry_create_finalize, container, false)
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
        if (context is CreateEntryFinalListener)
            activityApp = context
        else
            Log.d("INITLOG", "Error attaching listener in ${this::class}, context must " +
                    "implement ViewIngredientsFragment interface")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LOG", "${this::class} view created")
        view1 = requireView()
        createIngBackBut.setOnClickListener {
            activity?.onBackPressed()
        }
        setCalendar()
        setDateTimeListeners()
        setHideKeyboardInputLost()
        setFinishButton()
        setRecommendedIns()
    }

    private fun setRecommendedIns()
    {
        val insText = createEntryFinalRecIns
        val activity = activity as CreateEntryActivity
        if (activity.foodList.size == 0) insText.setText("0")
        else
        {
            val nut: Nutrition = Nutrition()
            for (ingRec in activity.foodList)
            {
                when(ingRec.first.whatType())
                {
                    FoodType.INGREDIENT ->
                    {
                        var ingredient = activity.app.dbHelper.getIngredient(ingRec.first.whatName())
                        nut.plusAssign(ingredient!!.nut / ingRec.second)
                    }

                    FoodType.RECIPE ->
                    {
                        var recipe = activity.app.dbHelper.getRecipe(ingRec.first.whatName())
                        nut.plusAssign(recipe!!.getNutrition() / ingRec.second)
                    }
                }
            }
            insText.setText(Math.round(nut.carbs / activity.app.user!!.INSULIN_PER10G).toString())
        }
    }

    private fun setCalendar()
    {
        Log.d("LOG", "${this::class} setting calendar" )
        setTime(CALENDAR.get(Calendar.HOUR_OF_DAY), CALENDAR.get(Calendar.MINUTE))
        val month = CALENDAR.get(Calendar.MONTH) + 1
        val day = CALENDAR.get(Calendar.DAY_OF_MONTH)
        val year = CALENDAR.get(Calendar.YEAR)
        val dateString = "$day/$month/$year"
        val dateText = view1.findViewById<EditText>(R.id.createEntryFinalDate)
        dateText?.setText(dateString)

    }

    private fun setDateTimeListeners()
    {
        Log.d("LOG", "${this::class} setting date and time events")
        val date = view1.findViewById<EditText>(R.id.createEntryFinalDate)
        val time = view1.findViewById<EditText>(R.id.createEntryFinalTime)
        date.setOnClickListener {
            activityApp.onDateClick()
        }
        time.setOnClickListener {
            activityApp.onTimeClick()
        }
    }

    private fun setHideKeyboardInputLost()
    {
        Log.d("LOG", "${this::class} setting input hide keyboard")
        createEntryFinalInsulinTaken.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }

        createEntryFinalBS.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }

        createEntryFinalNotes.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
    }

    private fun setFinishButton()
    {
        Log.d("LOG", "${this::class} setting finish button events")
        diaryEntryCreateFinalize.setOnClickListener {
            val insulinTakenString = createEntryFinalInsulinTaken.text.toString()
            val bloodSugarString = createEntryFinalBS.text.toString()
            val notesString = createEntryFinalNotes.text.toString()
            val insulinTaken: Int? = if (insulinTakenString.equals("")) null else insulinTakenString.toInt()
            val bloodSugar: Float? = if (bloodSugarString.equals("")) null else bloodSugarString.toFloat()
            val notes: String? = if (notesString.equals("")) null else notesString
            val foodEntry = FoodDiaryEntry(CALENDAR.timeInMillis)
            foodEntry.bloodSugar = bloodSugar
            foodEntry.insulinTaken = insulinTaken
            foodEntry.notes = notes
            Log.d("LOG", "Food entry created in ${this::class}: $foodEntry")
            val inserted = activityApp.onFinishEntry(foodEntry)
            val insertedString = if (inserted) "Successculy inserted food entry" else
                "Failed to insert food entry"
            Log.d("LOG", "Food entry database insertion '$insertedString' in ${this::class}\n$foodEntry")
            if (inserted) activityApp.onFinish()
            else  setDateTimeInvalid()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setDateTimeInvalid()
    {
        createEntryError.visibility = TextView.VISIBLE
        errorTime = CALENDAR.timeInMillis
    }

    private fun resetError()
    {
        createEntryError.visibility = TextView.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
    }

    fun setDate(date: String)
    {
        val dateText = view1.findViewById<EditText>(R.id.createEntryFinalDate)
        dateText?.setText(date)
        var dateArr = date.split("/")
        CALENDAR.set(Calendar.YEAR, dateArr[2].toInt())
        CALENDAR.set(Calendar.MONTH, dateArr[1].toInt()-1)
        CALENDAR.set(Calendar.DAY_OF_MONTH, dateArr[0].toInt())
        if (errorTime != CALENDAR.timeInMillis) resetError()
    }

    fun setTime(hourOfDay: Int, minute: Int)
    {
        val timeText = view1.findViewById<EditText>(R.id.createEntryFinalTime)
        CALENDAR.set(Calendar.HOUR_OF_DAY, hourOfDay)
        CALENDAR.set(Calendar.MINUTE, minute)
        var hourText = ""
        if (hourOfDay == 0 || hourOfDay == 12) hourText = "12"
        else if (hourOfDay > 12) hourText = (hourOfDay % 12).toString()
        else hourText = hourOfDay.toString()
        var minuteText = if (minute < 10) "0" + minute.toString() else minute.toString()
        var timeString = "$hourText:$minuteText"
        if (hourOfDay >= 12 && hourOfDay / 12 != 24) timeString += "pm" else timeString += "am"
        timeText.setText(timeString)
        if (errorTime != CALENDAR.timeInMillis) resetError()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOG", "")
    }
}