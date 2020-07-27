package com.example.malfoodware

import androidx.fragment.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.diary_entry_create_view.*


class CreateEntryViewFragment: Fragment() {
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

    companion object
    {
        val FRAGMENT_TAG = "createEntryViewFrag"
    }

    interface EntryViewListener
    {
        fun onEntryCreated(frag: CreateEntryViewFragment)
        fun onEntryViewFinish()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.diary_entry_create_view, container, false)
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
        activityApp.onEntryCreated(this)
        diaryEntryCreateViewBack.setOnClickListener {
            activity?.onBackPressed()
        }
        createEntryViewNext.setOnClickListener {
            activityApp.onEntryViewFinish()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view1 = view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
    }

    fun initDiaryEntry(list: List<Pair<FoodAccess, Float>>, user: User)
    {
        setDiaryViewWheels(list, user)
        setDiaryViewList(list)
    }

    fun setDiaryViewList(list: List<Pair<FoodAccess, Float>>)
    {
        rvEntries?.adapter = CreateEntryViewAdapter(list, this)
        rvEntries?.layoutManager = LinearLayoutManager(requireActivity())
    }

    fun clearSelection()
    {
        if (rvEntries != null) {
            val rv = rvEntries?.adapter as CreateEntryViewAdapter
            rv?.clearSelection()
        }
    }

    fun setDiaryViewWheels(entries: List<Pair<FoodAccess, Float>>, user: User)
    {
        var nutrition: Nutrition? = null
        if (entries.size > 0)
        {
            var entryIterator = entries.iterator()
            var entry: Pair<FoodAccess, Float>?
            while (entryIterator.hasNext())
            {
                entry = entryIterator.next()
                when(entry.first.whatType())
                {
                    FoodType.INGREDIENT ->
                    {
                        val ing = entry.first as Ingredient
                        if (nutrition == null)
                        {
                            nutrition = ing.nut / entry.second
                        }
                        else
                            nutrition.plusAssign( ing.nut / entry.second)
                    }
                    FoodType.RECIPE ->
                    {
                        val ing = entry.first as Recipe
                        if (nutrition == null)
                        {
                            nutrition = ing.getNutrition()!! / entry.second
                        }
                        else
                            nutrition.plusAssign( ing.getNutrition()!! / entry.second)
                    }
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