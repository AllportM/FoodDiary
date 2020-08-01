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
import kotlinx.android.synthetic.main.diary_entry_bottom_buttons.*
import kotlin.math.roundToInt


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
        fun addViewIngredientsFragFromMainEntry(type: FoodType)
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
        viewIngredientsME.setOnClickListener {
            activityApp.addViewIngredientsFragFromMainEntry(FoodType.INGREDIENT)
        }
        viewRecipeME.setOnClickListener {
            activityApp.addViewIngredientsFragFromMainEntry(FoodType.RECIPE)
        }
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
            rv.clicked = null
            EntryFocussedFragment.LAST_ENTRY = FoodDiaryEntry(1)
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
        progProtein.progress = (nutrition.protein / user.nutritionPerDay.protein * 100).roundToInt()
        progCarbs.progress = (nutrition.carbs / user.nutritionPerDay.carbs * 100).roundToInt()
        progEnergy.progress = (nutrition.energy / user.nutritionPerDay.energy * 100).roundToInt()
        progFat.progress = (nutrition.fat /user.nutritionPerDay.fat * 100).roundToInt()
        progFribre.progress = (nutrition.fibre / user.nutritionPerDay.fibre * 100).roundToInt()
        progSalt.progress = (nutrition.salt / user.nutritionPerDay.salt * 100).roundToInt()
        qtyCarbs.text = "${nutrition.carbs.roundToInt()} / ${user.nutritionPerDay.carbs.roundToInt()}g"
        qtyEnergy.text = "${nutrition.energy.roundToInt()} / ${user.nutritionPerDay.energy.roundToInt()}g"
        qtyFat.text = "${nutrition.fat.roundToInt()} / ${user.nutritionPerDay.fat.roundToInt()}g"
        qtyFibre.text = "${nutrition.fibre.roundToInt()} / ${user.nutritionPerDay.fibre.roundToInt()}g"
        qtyProtein.text = "${nutrition.protein.roundToInt()} / ${user.nutritionPerDay.protein.roundToInt()}g"
        qtySalt.text = "${nutrition.salt.round(2)} / ${user.nutritionPerDay.salt.round(2)}g"
    }

}