package com.example.malfoodware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.diary_entry_create.*
import java.util.*

class CreateEntryFragment: Fragment(), ViewIngredientsListInterface {

    companion object {
        val FRAGMENT_TAG = "createEntryFrament"
        var CALENDAR = Calendar.getInstance()
        val BAR_TITLE = "Create Diary Entry"
    }

    interface CreateEntryActivityListener {
        fun addViewIngredientsFragFromCreateEntry(type: FoodType, intf: ViewIngredientsListInterface)
        fun addShowEntryViewFrag(list: List<Pair<FoodAccess, Float>>)
    }

    init {
        CALENDAR = Calendar.getInstance()
    }

    lateinit var rvEntries: RecyclerView
    lateinit var activityApp: CreateEntryActivityListener
    var foodList = mutableListOf<Pair<FoodAccess, Float>>()
    lateinit var type: FoodType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.diary_entry_create, container, false)
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
        if (context is CreateEntryActivityListener)
            activityApp = context
        else
            Log.d(
                "INITLOG", "Error attaching listener in ${this::class}, context must " +
                        "implement ViewIngredientsFragment interface"
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LOG", "${this::class.java} view created")
        rvEntries = view.findViewById<View>(R.id.foodItemsEntry) as RecyclerView
        setFoodList()
        setClickListeners(view)
    }

    private fun setClickListeners(view: View)
    {
        val addIng = view.findViewById<Button>(R.id.addIngredientEntry)
        addIng.setOnClickListener {
            type = FoodType.INGREDIENT
            activityApp.addViewIngredientsFragFromCreateEntry(type, this)
        }
        val addRecipe = view.findViewById<Button>(R.id.addRecipeEntry)
        addRecipe.setOnClickListener {
            type = FoodType.RECIPE
            activityApp.addViewIngredientsFragFromCreateEntry(type, this)
        }
        val nextBut = view.findViewById<Button>(R.id.nextToNutsEntry)
        nextBut.setOnClickListener {
            activityApp.addShowEntryViewFrag(foodList)
        }
        createEntryBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    fun onItemRemove(name: String) {
        val newFoodList = mutableListOf<Pair<FoodAccess, Float>>()
        for (pair in foodList)
        {
            if (!pair.first.whatName().equals(name))
                newFoodList.add(pair)
        }
        foodList = newFoodList
        setFoodList()
    }

    private fun setFoodList()
    {
        rvEntries.adapter = CreateEntryListAdapter(foodList, this)
        rvEntries.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun updateSet(ingFrag: ViewInredientsFragment) {
        var set: SortedSet<String> = sortedSetOf()
        val activity = requireActivity() as MainActivity
        when(type)
        {
            FoodType.INGREDIENT -> set = activity.app.dbHelper.getIngredients()
            FoodType.RECIPE     -> set = activity.app.dbHelper.getRecipes()
        }
        for (pair in foodList)
        {
            var name = pair.first.whatName()
            when(type)
            {
                FoodType.INGREDIENT -> if (pair.first.whatType() == FoodType.INGREDIENT)
                    set.remove(name)
                FoodType.RECIPE     -> if (pair.first.whatType() == FoodType.RECIPE)
                    set.remove(name)
            }
        }
        ingFrag.set = set
        ingFrag.rvEntries.adapter = ViewIngredientsInsertAdapter(set, ingFrag)
    }

    override fun onInsertFullIng(name: String, qty: Float) {
        var activity = requireActivity() as MainActivity
        var ing: FoodAccess = when(type)
        {
            FoodType.INGREDIENT -> activity.app.dbHelper.getIngredient(name)!!
            FoodType.RECIPE     -> activity.app.dbHelper.getRecipe(name)!!
        }
        foodList.add(Pair<FoodAccess, Float>(ing, qty))
        rvEntries.adapter?.notifyDataSetChanged()
        activity.detachFragment(ViewInredientsFragment.FRAGMENT_TAG)
    }
}