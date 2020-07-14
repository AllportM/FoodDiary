package com.example.malfoodware

import FoodEntriesAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class FoodEntryFragmentMain: Fragment() {

    lateinit var view1: View
    lateinit var app: App
    lateinit var date: String
    lateinit var fm: FragmentManager
    val REQUEST_CODE = 11 // used for date message relay
    companion object
    {
        val FRAGMENT_TAG = "foodEntryFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.diary_entry_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view1 = view
        // sets the calendar button up
        var calendarButton = view1.findViewById<Button>(R.id.toolbar_calendar)
        fm = parentFragmentManager

        calendarButton.setOnClickListener {
            val newFragment: AppCompatDialogFragment = CalendarViewFragment()
            newFragment.setTargetFragment(this, REQUEST_CODE)
            newFragment.show(fm, "datePicker")
        }
        app = App(requireContext())
        app.login(app.dbHelper.getLoggedInUser()!!)
        date = app.dbHelper.getLoggedInSelectedDate()!!
        setToolBar()
        setToolbarDate(date)
        initDiaryEntry()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check for the results
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            date = data!!.getStringExtra("selectedDate")!!
            // set the value of the editText
            setToolbarDate(date)
            app.dbHelper.setLoggedInUser(app.user!!.uid, date)
            initDiaryEntry()
        }
    }

    fun setToolBar()
    {
        val toolbar: android.widget.Toolbar = view1.findViewById(R.id.toolbar3)
        activity?.setActionBar(toolbar)
    }

    fun setToolbarDate(date: String)
    {
        activity?.actionBar?.setTitle(date)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        println("ENTRY VIEW RESUMED")
        println(date + ", " + app.user)
    }

    fun initDiaryEntry()
    {
        var entries = app.dbHelper.getDiaryEntriesDate(app.user!!.uid, date)
        setDiaryViewWheels(entries)
        setDiaryViewList(entries)
    }

    @SuppressLint("ResourceAsColor")
    fun setDiaryViewList(entries: SortedSet<FoodDiaryEntry>)
    {
        val rvEntries = view1.findViewById<View>(R.id.rvEntries) as RecyclerView
        val list = app.getListOfEntries(date)
        list.addAll(app.getListOfEntries(date))
        rvEntries.adapter = FoodEntriesAdapter(list, fm)
        rvEntries.layoutManager = LinearLayoutManager(activity)
    }

    fun setDiaryViewWheels(entries: SortedSet<FoodDiaryEntry>)
    {
        val progProtein = view1.findViewById<ProgressBar>(R.id.progressBarProtein)
        val progCarbs = view1.findViewById<ProgressBar>(R.id.progressBarCarbs)
        val progEnergy = view1.findViewById<ProgressBar>(R.id.progressBarEnergy)
        val progFat = view1.findViewById<ProgressBar>(R.id.progressBarFat)
        val progSalt = view1.findViewById<ProgressBar>(R.id.progressBarSalt)
        val progFribre = view1.findViewById<ProgressBar>(R.id.progressBarFibre)
        val qtyProtein = view1.findViewById<TextView>(R.id.textViewProteinQty)
        val qtyCarbs = view1.findViewById<TextView>(R.id.textViewCarbsQty)
        val qtyEnergy = view1.findViewById<TextView>(R.id.textViewEnergyQty)
        val qtyFat = view1.findViewById<TextView>(R.id.textViewFatQty)
        val qtySalt = view1.findViewById<TextView>(R.id.textViewSaltQty)
        val qtyFibre = view1.findViewById<TextView>(R.id.textViewFibreQty)
        var nutrition: Nutrition? = null
        if (entries.size > 0)
        {
            var entryIterator = entries.iterator()
            var entry: FoodDiaryEntry?
            while (entryIterator.hasNext())
            {
                entry = entryIterator.next()
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
            }
        }
        if (nutrition == null) nutrition = Nutrition(0f,0f,0f,0f,
            0f,0f,0.1f)
        progProtein.progress = (nutrition.protein / app.user!!.nutritionPerDay.protein * 100).toInt()
        progCarbs.progress = (nutrition.carbs / app.user!!.nutritionPerDay.carbs * 100).toInt()
        progEnergy.progress = (nutrition.energy / app.user!!.nutritionPerDay.energy * 100).toInt()
        progFat.progress = (nutrition.fat / app.user!!.nutritionPerDay.fat * 100).toInt()
        progFribre.progress = (nutrition.fibre / app.user!!.nutritionPerDay.fibre * 100).toInt()
        progSalt.progress = (nutrition.salt / app.user!!.nutritionPerDay.salt * 100).toInt()
        qtyCarbs.text = "${nutrition.carbs.toInt()} / ${app.user!!.nutritionPerDay.carbs.toInt()}g"
        qtyEnergy.text = "${nutrition.energy.toInt()} / ${app.user!!.nutritionPerDay.energy.toInt()}g"
        qtyFat.text = "${nutrition.fat.toInt()} / ${app.user!!.nutritionPerDay.fat.toInt()}g"
        qtyFibre.text = "${nutrition.fibre.toInt()} / ${app.user!!.nutritionPerDay.fibre.toInt()}g"
        qtyProtein.text = "${nutrition.protein.toInt()} / ${app.user!!.nutritionPerDay.protein.toInt()}g"
        qtySalt.text = "${nutrition.salt.toInt()} / ${app.user!!.nutritionPerDay.salt.toInt()}g"
    }

}