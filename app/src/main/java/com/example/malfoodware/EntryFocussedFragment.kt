package com.example.malfoodware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment


class EntryFocussedFragment(val entry: FoodDiaryEntry, val user: User) : Fragment() {

    companion object {
        val FRAGMENT_TAG = "entry_focussed"
        val BUNDLE_MILLI = "entry_millis"
        var LAST_ENTRY = FoodDiaryEntry()
        var LAST_USER = User("NA")
    }

    constructor() : this(LAST_ENTRY, LAST_USER)

    init {
        LAST_ENTRY = entry
        LAST_USER = user
    }

    interface EntryFocussedListener
    {
        fun showEntryDetails(entry: FoodDiaryEntry)
    }

    lateinit var activityApp: EntryFocussedListener
    lateinit var progProtein: ProgressBar
    lateinit var progCarbs: ProgressBar
    lateinit var progEnergy: ProgressBar
    lateinit var progFat: ProgressBar
    lateinit var progSalt: ProgressBar
    lateinit var progFribre : ProgressBar
    lateinit var qtyProtein: TextView
    lateinit var qtyCarbs: TextView
    lateinit var qtyEnergy: TextView
    lateinit var qtyFat: TextView
    lateinit var qtySalt: TextView
    lateinit var qtyFibre: TextView
    lateinit var view1: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.diary_entry_view_focussed, container, false)
        return v
    }

    fun getLastEntry(): FoodDiaryEntry
    {
        return LAST_ENTRY
    }

    override fun onStart() {
        super.onStart()
        var view = view1
        progProtein = view.findViewById<ProgressBar>(R.id.progressBarProteinInner)
        progCarbs = view.findViewById<ProgressBar>(R.id.progressBarCarbsInner)
        progEnergy = view.findViewById<ProgressBar>(R.id.progressBarEnergyInner)
        progFat = view.findViewById<ProgressBar>(R.id.progressBarFatInner)
        progSalt = view.findViewById<ProgressBar>(R.id.progressBarSaltInner)
        progFribre = view.findViewById<ProgressBar>(R.id.progressBarFibreInner)
        qtyProtein = view.findViewById<TextView>(R.id.textViewProteinFocussed)
        qtyCarbs = view.findViewById<TextView>(R.id.textViewCarbsFocussed)
        qtyEnergy = view.findViewById<TextView>(R.id.textViewEnergyFocussed)
        qtyFat = view.findViewById<TextView>(R.id.textViewFatFocussed)
        qtySalt = view.findViewById<TextView>(R.id.textViewSaltFocussed)
        qtyFibre = view.findViewById<TextView>(R.id.textViewFibreFocussed)
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
        if (nutrition == null) nutrition = Nutrition(0f,0f,0f,0f,
            0f,0f,0.1f)
        progProtein.progress = (nutrition.protein / user.nutritionPerDay.protein * 100).toInt()
        progCarbs.progress = (nutrition.carbs / user.nutritionPerDay.carbs * 100).toInt()
        progEnergy.progress = (nutrition.energy / user.nutritionPerDay.energy * 100).toInt()
        progFat.progress = (nutrition.fat /user.nutritionPerDay.fat * 100).toInt()
        progFribre.progress = (nutrition.fibre / user.nutritionPerDay.fibre * 100).toInt()
        progSalt.progress = (nutrition.salt / user.nutritionPerDay.salt * 100).toInt()
        qtyCarbs.text = "${nutrition.carbs.toInt()}g"
        qtyEnergy.text = "${nutrition.energy.toInt()}g"
        qtyFat.text = "${nutrition.fat.toInt()}g"
        qtyFibre.text = "${nutrition.fibre.toInt()}g"
        qtyProtein.text = "${nutrition.protein.toInt()}g"
        qtySalt.text = "${nutrition.salt.toInt()}g"

        var button = view1.findViewById<Button>(R.id.foodEntryMoreDetails)
        button.setOnClickListener {
            activityApp.showEntryDetails(entry)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view1 = view
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EntryFocussedListener)
            activityApp = context
        else
            Log.d("INITLOG", "Error attaching listener in ${this::class}, context must " +
                    "implement onLogin interface")
    }

}