package com.example.malfoodware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment


class EntryFocussedDetailsFragment(val entry: FoodDiaryEntry, val user: User) : Fragment() {

    companion object {
        val FRAGMENT_TAG = "entry_focussed_details"
        val BUNDLE_MILLI = "entry_millis"
        var LAST_ENTRY = FoodDiaryEntry()
        var LAST_USER = User("NA")
    }

    constructor() : this(LAST_ENTRY, LAST_USER)

    init {
        LAST_ENTRY = entry
        LAST_USER = user
    }

    interface EntryDetailsListener
    {
        fun hideEntryDetailed()
    }

    lateinit var view1: View
    lateinit var insulinTaken: TextView
    lateinit var recIns: TextView
    lateinit var bs: TextView
    lateinit var notes: TextView
    lateinit var activityApp: EntryDetailsListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.diary_entry_view_focussed_details, container, false)
        return v
    }

    // initilaizes ViewTexts and sets the text values for given entry
    override fun onStart() {
        super.onStart()
        insulinTaken = view1.findViewById(R.id.entryDetailsInsTaken)
        bs = view1.findViewById(R.id.entryDetailsBS)
        recIns = view1.findViewById(R.id.entryDetailsRecIns)
        notes = view1.findViewById(R.id.entryDetailsNotes)
        val insTakenStr = if (entry.insulinTaken == null) "N/A" else entry.insulinTaken.toString()
        insulinTaken.setText(insTakenStr)

        // finds total carbs value
        var nutrition: Nutrition? = null
        for (recipe in entry.recipes)
        {
            if (nutrition == null)
            {
                nutrition = recipe.key.getNutrition()!! / recipe.value
            }
            else {
                nutrition.plusAssign(recipe.key.getNutrition()!! / recipe.value)
            }
        }
        for (ingredient in entry.ingredients)
        {
            if (nutrition == null)
            {
                nutrition = ingredient.key.nut / ingredient.value
            }
            else
                nutrition.plusAssign( ingredient.key.nut / ingredient.value)
        }
        val recInsStr = if (entry.ingredients.size == 0 && entry.recipes.size == 0) "N/A" else
            Math.round(nutrition!!.carbs / user.INSULIN_PER10G).toString()
        recIns.setText(recInsStr)

        val bsStr = if (entry.bloodSugar == null) "N/A" else Math.round(entry.bloodSugar!!).toString()
        bs.setText(bsStr)

        val noteStr = if (entry.notes == null) "N/A" else entry.notes
        notes.setText(noteStr)
        var button = view1.findViewById<Button>(R.id.foodEntryLessDetails)
        button.setOnClickListener {
            activityApp.hideEntryDetailed()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EntryDetailsListener)
            activityApp = context
        else
            Log.d("INITLOG", "Error attaching listener in ${this::class}, context must " +
                    "implement onLogin interface")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view1 = view
    }

    override fun onResume() {
        super.onResume()
    }


}