package com.example.malfoodware

import EntriesAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class EntryFragmentMain: Fragment() {
    lateinit var  activityApp: EntryViewListener
    lateinit var progProtein: ProgressBar
    lateinit var  progCarbs: ProgressBar
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
    var rvEntries: RecyclerView? = null
    lateinit var view1: View
    var instanceID: Int

    init {
        instanceID = INSTANCE_ID
        INSTANCE_ID++
    }
    companion object
    {
        val FRAGMENT_TAG = "foodEntryFragment"
        var INSTANCE_ID = 0
    }

    interface EntryViewListener
    {
        fun onEntryCreated()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.diary_entry_view, container, false)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EntryViewListener)
            activityApp = context
        else
            Log.d("INITLOG", "Error attaching listener in LoginFragment, context must implement onLogin interface")
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
    }

    override fun onStart() {
        super.onStart()
        Log.d("LOG", "FoodFragmentMain started $instanceID")
        var view = view1
        progProtein = view.findViewById<ProgressBar>(R.id.progressBarProtein)
        progCarbs = view.findViewById<ProgressBar>(R.id.progressBarCarbs)
        progEnergy = view.findViewById<ProgressBar>(R.id.progressBarEnergy)
        progFat = view.findViewById<ProgressBar>(R.id.progressBarFat)
        progSalt = view.findViewById<ProgressBar>(R.id.progressBarSalt)
        progFribre = view.findViewById<ProgressBar>(R.id.progressBarFibre)
        qtyProtein = view.findViewById<TextView>(R.id.textViewProteinQty)
        qtyCarbs = view.findViewById<TextView>(R.id.textViewCarbsQty)
        qtyEnergy = view.findViewById<TextView>(R.id.textViewEnergyQty)
        qtyFat = view.findViewById<TextView>(R.id.textViewFatQty)
        qtySalt = view.findViewById<TextView>(R.id.textViewSaltQty)
        qtyFibre = view.findViewById<TextView>(R.id.textViewFibreQty)
        rvEntries = view.findViewById<View>(R.id.rvEntries) as RecyclerView
        activityApp.onEntryCreated()
    }

    override fun onStop() {
        super.onStop()
        Log.d("LOG", "FoodEntryMain stopped $instanceID")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOG", "FoodEntryMain destroyed $instanceID")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view1 = view
        Log.d("LOG", "FoodFragmentMain view created $instanceID")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
    }

    fun initDiaryEntry(list: MutableList<FoodDiaryEntry>, user: User)
    {
        setDiaryViewWheels(list, user)
        setDiaryViewList(list)
    }

    fun setDiaryViewList(list: MutableList<FoodDiaryEntry>)
    {
        rvEntries?.adapter = EntriesAdapter(list)
        rvEntries?.layoutManager = LinearLayoutManager(requireActivity())
    }

    fun clearSelection()
    {
        if (rvEntries != null) {
            val rv = rvEntries?.adapter as EntriesAdapter
            rv?.clearSelection()
        }
    }

    fun setDiaryViewWheels(entries: MutableList<FoodDiaryEntry>, user: User)
    {
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
        progProtein.progress = (nutrition.protein / user.nutritionPerDay.protein * 100).toInt()
        progCarbs.progress = (nutrition.carbs / user.nutritionPerDay.carbs * 100).toInt()
        progEnergy.progress = (nutrition.energy / user.nutritionPerDay.energy * 100).toInt()
        progFat.progress = (nutrition.fat /user.nutritionPerDay.fat * 100).toInt()
        progFribre.progress = (nutrition.fibre / user.nutritionPerDay.fibre * 100).toInt()
        progSalt.progress = (nutrition.salt / user.nutritionPerDay.salt * 100).toInt()
        qtyCarbs.text = "${nutrition.carbs.toInt()} / ${user.nutritionPerDay.carbs.toInt()}g"
        qtyEnergy.text = "${nutrition.energy.toInt()} / ${user.nutritionPerDay.energy.toInt()}g"
        qtyFat.text = "${nutrition.fat.toInt()} / ${user.nutritionPerDay.fat.toInt()}g"
        qtyFibre.text = "${nutrition.fibre.toInt()} / ${user.nutritionPerDay.fibre.toInt()}g"
        qtyProtein.text = "${nutrition.protein.toInt()} / ${user.nutritionPerDay.protein.toInt()}g"
        qtySalt.text = "${nutrition.salt.toInt()} / ${user.nutritionPerDay.salt.toInt()}g"
    }

}