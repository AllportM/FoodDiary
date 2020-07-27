package com.example.malfoodware

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CreateEntryActivity:
        AppCompatActivity(),
        CreateEntryListAdapter.CreateEntryListAdapterListener,
        ViewInredientsFragment.ViewIngredientsFragmentListener,
        ViewIngredientsAdapter.ViewIngredientsAdapterListener,
        CreateEntryViewFragment.EntryViewListener,
        CreateEntryViewAdapter.CreateEntryViewListener,
        CreateEntryFinalFragment.CreateEntryFinalListener,
        CalendarViewFragment.DatePickerListener,
        TimeDialoueFragment.TimeDialogueInterface
{
    private var type = FoodType.INGREDIENT
    private val FRAGMENT_EP = R.id.createEntryFragmentEntry
    val app: App= App(this)
    lateinit var rvEntries: RecyclerView
    var foodList = mutableListOf<Pair<FoodAccess, Float>>()

    companion object
    {
        val INREDIENT_REQ_CODE = 333
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_entry_create)
        setSupportActionBar(findViewById(R.id.createEntryTolbar))
        rvEntries = findViewById<View>(R.id.foodItemsEntry) as RecyclerView
        setTitle("Create Diary Entry")
        val addIng = findViewById<Button>(R.id.addIngredientEntry)
        addIng.setOnClickListener {
            type = FoodType.INGREDIENT
            addViewIngredientsFrag()
        }
        val addRecipe = findViewById<Button>(R.id.addRecipeEntry)
        addRecipe.setOnClickListener {
            type = FoodType.RECIPE
            addViewIngredientsFrag()
        }
        val nextBut = findViewById<Button>(R.id.nextToNutsEntry)
        nextBut.setOnClickListener {
            showEntryView()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        val uname = intent.getStringExtra("UNAME")
        if (uname != null) {
            app.login(uname)
            if (app.user != null)
                Log.d("LOG", "Login via create entry activity successfull")
        }
        setFoodList()
    }

    override fun onBackPressed() {
        val backStackCount = supportFragmentManager.backStackEntryCount
        if (backStackCount > 0) {
            val fragTopStackTag =
                supportFragmentManager.getBackStackEntryAt(backStackCount - 1)
                    .name
            if (fragTopStackTag.equals(EntryFocussedFragment.FRAGMENT_TAG)) {
                val frag = supportFragmentManager.findFragmentByTag(CreateEntryViewFragment.FRAGMENT_TAG) as CreateEntryViewFragment
                frag.clearSelection()
            }
            supportFragmentManager.popBackStack()
        }
        else
            super.onBackPressed()
    }

    override fun onItemRemove(name: String) {
        val newFoodList = mutableListOf<Pair<FoodAccess, Float>>()
        for (pair in foodList)
        {
            if (!pair.first.whatName().equals(name))
                newFoodList.add(pair)
        }
        foodList = newFoodList
        setFoodList()
    }

    fun attachFragment(fragment: Fragment, tag: String)
    {
        var transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
        transaction.add(FRAGMENT_EP, fragment, tag)
            .addToBackStack(tag)
        transaction.show(fragment)
        transaction.commit()
    }

    fun detachFragment(tag: String)
    {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        Log.d("LOG", "Attempting to remove fragment $tag")
        if (fragment != null) {
            Log.d("LOG", "found fragment $tag")
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            transaction.remove(fragment)
            transaction.commit()
            supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun setFoodList()
    {
        rvEntries.adapter = CreateEntryListAdapter(foodList)
        rvEntries.layoutManager = LinearLayoutManager(this)
    }

    /**
     * ViewIngredientsFragment stuff
     */
    // resets/sets ingredient fragments data set and recycler view adapter
    override fun updateSet(ingFrag: ViewInredientsFragment) {
        var set: SortedSet<String> = sortedSetOf()
        when(type)
        {
            FoodType.INGREDIENT -> set = app.dbHelper.getIngredients()
            FoodType.RECIPE     -> set = app.dbHelper.getRecipes()
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
        ingFrag.rvEntries.adapter = ViewIngredientsAdapter(set, ingFrag)
    }

    // attaches new ViewIngredients frament
    private fun addViewIngredientsFrag()
    {
        val ingredientsFrag = ViewInredientsFragment()
        when (type)
        {
            FoodType.INGREDIENT -> ingredientsFrag.type = FoodType.INGREDIENT
            FoodType.RECIPE -> ingredientsFrag.type = FoodType.RECIPE
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(FRAGMENT_EP, ingredientsFrag, ViewInredientsFragment.FRAGMENT_TAG)
        transaction.addToBackStack(ViewInredientsFragment.FRAGMENT_TAG)
        transaction.commit()
    }

    override fun onCreateIngredient() {
        val intent = Intent(this, CreateFoodActivity::class.java)
        intent.putExtra("UNAME", app.user!!.uid)
        intent.putExtra("type", type.toString())
        startActivity(intent)
    }

    override fun onCreateRecipe() {
        val intent = Intent(this, CreateFoodActivity::class.java)
        intent.putExtra("UNAME", app.user!!.uid)
        intent.putExtra("type", type.toString())
        startActivity(intent)
    }

    // responds to add existing ingredient button click to bring qty popup
    override fun onInsertIngredient(name: String, ingFrag: ViewInredientsFragment) {
        val fm = supportFragmentManager
        val fragment = QuantityPopupFrag()
        fragment.setTargetFragment(ingFrag, INREDIENT_REQ_CODE)
        intent.putExtra("name", name)
        val ingRec: FoodAccess
        when(type)
        {
            FoodType.INGREDIENT -> ingRec = app.dbHelper.getIngredient(name)!!
            FoodType.RECIPE -> ingRec = app.dbHelper.getRecipe(name)!!
        }
        intent.putExtra("defQty", ingRec.whatServing())
        fragment.show(fm, QuantityPopupFrag.QTY_POPUP_FRAG_TAG)
    }

    override fun onInsertFullIng(name: String, qty: Float) {
        var ing: FoodAccess = when(type)
        {
            FoodType.INGREDIENT -> app.dbHelper.getIngredient(name)!!
            FoodType.RECIPE     -> app.dbHelper.getRecipe(name)!!
        }
        foodList.add(Pair<FoodAccess, Float>(ing, qty))
        rvEntries.adapter?.notifyDataSetChanged()
        detachFragment(ViewInredientsFragment.FRAGMENT_TAG)
    }

    /**
     * Create entry view stuffs
     */

    private fun showEntryView()
    {
        attachFragment(CreateEntryViewFragment(), CreateEntryViewFragment.FRAGMENT_TAG)
    }

    override fun onEntryCreated(frag: CreateEntryViewFragment) {
        frag.initDiaryEntry(foodList, app.user!!)
    }

    // create entry view next button clicked, add new finalize fragment to stack
    override fun onEntryViewFinish() {
        attachFragment(finalFrag, CreateEntryFinalFragment.FRAGMENT_TAG)
    }

    override fun showFoodEntryFocussed(entry: Pair<FoodAccess, Float>) {
        val foodEntry = FoodDiaryEntry()
        when(entry.first.whatType())
        {
            FoodType.RECIPE -> foodEntry.addRecipe(entry.first as Recipe, entry.second)
            FoodType.INGREDIENT -> foodEntry.addIngredient(entry.first as Ingredient, entry.second)
        }
        val frag = EntryFocussedFragment(foodEntry, app.user!!)
        attachFragment(frag, EntryFocussedFragment.FRAGMENT_TAG)
    }

    override fun hideFoodEntryFocussed(frag: CreateEntryViewFragment) {
        frag.clearSelection()
        detachFragment(EntryFocussedFragment.FRAGMENT_TAG)
    }

    // final create entry fragment events
    val finalFrag = CreateEntryFinalFragment()
    override fun onDateClick() {
        CalendarViewFragment(CreateEntryFinalFragment.CALENDAR.timeInMillis).show(supportFragmentManager, CalendarViewFragment.CALENDAR_FRAGMENT_TAG)
    }

    // populates an entries ingredients/recipes from foodList and attempts to insert into db
    override fun onFinishEntry(entry: FoodDiaryEntry): Boolean {
        for (ingRec in foodList)
        {
            when(ingRec.first.whatType())
            {
                FoodType.INGREDIENT ->
                {
                    var ingredient = app.dbHelper.getIngredient(ingRec.first.whatName())
                    entry.addIngredient(ingredient!!, ingRec.second)
                }

                FoodType.RECIPE ->
                {
                    var recipe = app.dbHelper.getRecipe(ingRec.first.whatName())
                    entry.addRecipe(recipe!!, ingRec.second)
                }
            }
        }
        return app.dbHelper.insertDiaryEntry(entry, app.user!!.uid)
    }

    override fun onFinish() {
        finish()
    }

    override fun onTimeClick() {
        TimeDialoueFragment().show(supportFragmentManager, TimeDialoueFragment.FRAGMENT_TAG)
    }

    // calendar fragment events
    override fun calDateClickedEntry(date: String) {
        finalFrag.setDate(date)
    }

    override fun onCalDismiss() {
        detachFragment(CalendarViewFragment.CALENDAR_FRAGMENT_TAG)
    }

    override fun onTimeClicked(hourOfDay: Int, minute: Int) {
        finalFrag.setTime(hourOfDay, minute)
    }
}