package com.example.malfoodware

import EntriesAdapter
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.my_toolbar.*
import kotlinx.coroutines.internal.artificialFrame
import java.lang.Exception
import java.util.*

class MainActivity :
        AppCompatActivity(),
        LoginFragment.LoginFragmentListener,
        EntryFragmentMain.EntryViewListener,
        CalendarViewFragment.DatePickerListener,
        CalendarUseIF,
        EntriesAdapter.
        FoodEntryListListener,
        EntryFocussedDetailsFragment.EntryDetailsListener,
        EntryFocussedFragment.EntryFocussedListener,
        CreateEntryFragment.CreateEntryActivityListener,
        ViewInredientsFragment.ViewIngredientsFragmentListener,
        ViewIngredientsInsertAdapter.ViewIngredientsAdapterListener,
        ViewIngredientEditAdapter.ViewIngredientsEditAdapterListener,
        CreateEntryViewFragment.EntryViewListener,
        CreateEntryViewAdapter.CreateEntryViewListener,
        CreateEntryFinalFragment.CreateEntryFinalListener,
        CreateRecipeFragment.CreateRecipeActivityListener,
        CreateIngFragment.CreateInredientActivityListener
{
    lateinit var app:  App
    private val LOGIN_FRAGMENT_ENTRY = R.id.fragmentEntryPoint
    private val ENTRY_FRAGMENT_ENTRY = R.id.entryFragmentEntry
    private val CALENDAR_VIEW_TAG = "calendarView"
    private val CREATE_REC_TITLE = "Create Recipe"
    private val CREATE_ENTRY_TITLE = "Create Diary Entry"
    private val CREATE_ING_TITLE = "Create Ingredient"
    lateinit var loginfrag: LoginFragment
    lateinit var entryFrag: EntryFragmentMain
    private val titleStack = mutableListOf<String>()

    companion object {
        var COL_LIST_LIGHT = 0
        var COL_LIST_DARK = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LOG", "Main activity created")
        app = App(applicationContext)
        setContentView(R.layout.content_main)
        setToolBarEmpty()
        loginfrag = LoginFragment()
        entryFrag = EntryFragmentMain()
        if (checkUserSignedIn())
        {
            setUserClickedDateToday()
        }
        COL_LIST_LIGHT = ResourcesCompat.getColor(resources, R.color.list_light_grey,null)
        COL_LIST_DARK = ResourcesCompat.getColor(resources, R.color.list_dark_grey, null)
    }

    fun checkUserSignedIn(): Boolean
    {
        val uid = app.dbHelper.getLoggedInUser()
        if (uid != null)
        {
            Log.d("LOG", "Existing user found, logging in")
            app.login(uid)
            setContentView(R.layout.my_toolbar)
            launchEntryView()
            return true
        }
        else {
            launchLoginView()
            return false
        }


    }

    private fun clearBackStack()
    {
        for (fragment in supportFragmentManager.fragments)
        {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onStart() {
        Log.d("LOG", "Main Activity started")
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        Log.d("LOG", "Main Activity paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LOG", "Main Activity stopped")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LOG", "Main Activity resumed")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOG", "Main Activity Destroyed")
    }


    // activates actions required specific to fragments being popped i.e changing titles
    // updating adapter lists
    override fun onBackPressed() {
        Log.d("LOG", "${this::class.java} back pressed")
        val backStackCount = supportFragmentManager.backStackEntryCount
        if (backStackCount == 1)
        {
            Log.d("LOG", "${this::class.java} only 1 item on back stack, setting main entry")
            resetToolBarToEntryView()
            clearEntryFragSelection()
            super.onBackPressed()
            return
        }
        if (backStackCount > 1)
        {
            Log.d("LOG", "${this::class.java} more than 1 item on back stack, resetting toolbars")
            // supportfragments.fragments has 1 more member as entry main was never
            // added to the backstack, therefor -2 on backstack entry = fragments[-1]
            var fragSecondStackTag =
                supportFragmentManager.getBackStackEntryAt(backStackCount - 2)
                    .name
            when (fragSecondStackTag)
            {
                EntryFocussedDetailsFragment.FRAGMENT_TAG,
                    EntryFragmentMain.FRAGMENT_TAG,
                    EntryFocussedFragment.FRAGMENT_TAG ->
                        setToolBarUserDate()

                CreateEntryFragment.FRAGMENT_TAG ->
                {
                    val frag = supportFragmentManager.fragments.get(backStackCount-1)
                        as CreateEntryFragment
                    setTitleFragmentTag(frag, CreateEntryFragment.FRAGMENT_TAG)
                }

                ViewInredientsFragment.FRAGMENT_TAG ->
                {
                    val frag = supportFragmentManager.fragments.get(backStackCount-1)
                            as ViewInredientsFragment
                    setTitleFragmentTag(frag, ViewInredientsFragment.FRAGMENT_TAG)
                }

                CreateRecipeFragment.FRAGMENT_ID ->
                {
                    val frag = supportFragmentManager.fragments.get(backStackCount-1)
                            as CreateRecipeFragment
                    setTitleFragmentTag(frag, CreateRecipeFragment.FRAGMENT_ID)
                }
            }
        }
        if (backStackCount > 0) {
            Log.d("LOG", "${this::class.java} at least 1 item on backstack, refreshing adapter lists")
            var fragTopStackTag =
                supportFragmentManager.getBackStackEntryAt(backStackCount - 1)
                    .name
            when(fragTopStackTag.toString())
            {
                EntryFocussedFragment.FRAGMENT_TAG ->
                {
                    clearEntryFragSelection()
                    try {
                        val frag =
                            supportFragmentManager.findFragmentByTag(CreateEntryViewFragment.FRAGMENT_TAG)
                                    as CreateEntryViewFragment
                        frag.clearSelection()
                    }
                    catch (e: Exception){}
                }

                CreateIngFragment.FRAGMENT_ID ->
                {
                    // attempts to refresh adapter view in view ingredients frag
                    if (backStackCount > 2)
                    {
                        for (fragment in supportFragmentManager.fragments)
                        {
                            try {
                                val inViewFrag =
                                        fragment as ViewInredientsFragment
                                inViewFrag.onResume()
                            } catch (inore: Exception){}
                        }
                    }
                }

                // updates adapter
                CreateRecipeFragment.FRAGMENT_ID ->
                {
                    try {
                        var fragSecondTopStackTag =
                            supportFragmentManager.getBackStackEntryAt(backStackCount - 2)
                                .name
                        val inViewFrag =
                            supportFragmentManager.findFragmentByTag(fragSecondTopStackTag)
                                as ViewInredientsFragment
                        inViewFrag.onResume()
                    } catch (ignore: Exception){}
                }
            }
            super.onBackPressed()
        }
        else {
            super.onBackPressed()
        }
    }



    /**
     * Utility functions for main activity
     */
    private fun attachFragment(fragment: Fragment, tag: String)
    {
        Log.d("LOG", "${this::class.java} attaching fragment ${fragment::class.java}")
        var transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
        transaction.add(ENTRY_FRAGMENT_ENTRY, fragment, tag)
            .addToBackStack(tag)
        transaction.show(fragment)
        transaction.commit()
        if (!tag.equals(MenuFragment.FRAG_TAG))
            setTitleFragmentTag(fragment, tag)
    }

    fun setTitleFragmentTag(fragment: Fragment, tag: String)
    {
        Log.d("LOG", "${this::class.java} setting title bar for $tag")
        hideMenuButtons()
        when (tag)
        {
            // create entry
            CreateEntryFragment.FRAGMENT_TAG,
            CreateEntryViewFragment.FRAGMENT_TAG,
            CreateEntryFinalFragment.FRAGMENT_TAG ->
                supportActionBar?.setTitle(CreateEntryFragment.BAR_TITLE)

            // view ingredients
            ViewInredientsFragment.FRAGMENT_TAG ->
            {
                val frag = fragment as ViewInredientsFragment
                when (frag.type)
                {
                    FoodType.RECIPE -> supportActionBar?.setTitle("View Recipes")
                    FoodType.INGREDIENT -> supportActionBar?.setTitle("View Ingredients")
                }
            }

            CreateIngFragment.FRAGMENT_ID -> {
                val frag = fragment as CreateIngFragment
                if (frag.ing == null)
                    supportActionBar?.setTitle("Create Ingredient")
                else
                    supportActionBar?.setTitle("Ammend Ingredient")
            }

            CreateRecipeFragment.FRAGMENT_ID ->{
                val frag = fragment as CreateRecipeFragment
                if (frag.recipe == null)
                    supportActionBar?.setTitle("Create Recipe")
                else
                    supportActionBar?.setTitle("Ammend Recipe")
            }

            // main entry view
            EntryFragmentMain.FRAGMENT_TAG ,
            EntryFocussedFragment.FRAGMENT_TAG ,
            EntryFocussedDetailsFragment.FRAGMENT_TAG ->
                setToolBarUserDate()

            ViewIngredientEntryMiddleman.FRAGMENT_TAG ->
            {
                val frag = fragment as ViewIngredientEntryMiddleman
                when (frag.type)
                {
                    FoodType.RECIPE -> supportActionBar?.setTitle("View Recipes")
                    FoodType.INGREDIENT -> supportActionBar?.setTitle("View Ingredients")
                }
            }
        }
    }

    fun detachFragment(tag: String)
    {
        Log.d("LOG", "${this::class.java} detaching fragment $tag")
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            transaction.remove(fragment)
            transaction.commit()
            supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun clearEntryFragSelection()
    {
        Log.d("LOG", "${this::class.java} clearing entry selected")
        EntryFocussedFragment.LAST_ENTRY = FoodDiaryEntry(1)
        entryFrag.clearSelection()
    }


    private fun setToolBarEmpty()
    {
        setSupportActionBar(findViewById(R.id.toolbar2))
    }

    private fun setToolBarUserDate()
    {
        val userDate = app.dbHelper.getLoggedInSelectedDate()!!
        supportActionBar!!.setTitle(userDate)
        showMenuButtons()
    }


    private fun setToolBarEntryView()
    {
        Log.d("LOG", "${this::class.java} setting toolbar entry view")
        val toolbar: Toolbar = findViewById(R.id.toolbar3)
        setSupportActionBar(toolbar)
        var calendarButton = findViewById<Button>(R.id.toolbar_calendar)
        calendarButton.setOnClickListener {
            Log.d("LOG", "Calendar button clicked")
            launchCalendarDialogueMainAct()
        }
        toolbar_menu.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag(MenuFragment.FRAG_TAG) == null)
                attachFragment(MenuFragment(), MenuFragment.FRAG_TAG)
            else
                detachFragment(MenuFragment.FRAG_TAG)
        }
    }

    private fun resetToolBarToEntryView()
    {
        setEntryViewDate()
        showMenuButtons()
    }

    private fun setUserClickedDateToday()
    {
        Log.d("LOG", "${this::class.java} setting user clicked today")
        val date = Calendar.getInstance()
        val dayString = date.get(Calendar.DAY_OF_MONTH)
        val monthString = date.get(Calendar.MONTH) + 1
        val yearString = date.get(Calendar.YEAR)
        val dateString = "$dayString/$monthString/$yearString"
        app.dbHelper.setLoggedInUser(app.user!!.uid, dateString)
    }

    private fun hideMenuButtons()
    {
        toolbar_calendar.visibility = Button.GONE
    }

    private fun showMenuButtons()
    {
        toolbar_calendar.visibility = Button.VISIBLE
        toolbar_menu.visibility = Button.VISIBLE
    }

    /**
     * Login Stuffs
     */
    override fun onLogin(uid: String) {
        if (!app.login(uid)) {
            Log.d("LOG", "${this::class} failed to login with uid: $uid")
            Toast.makeText(
                this, "Failed to login with $uid, no such user",
                Toast.LENGTH_SHORT
            ).show()
        }
        else
        {
            Log.d("LOG", "${this::class} logging in with uid: $uid")
            setUserClickedDateToday()
            checkUserSignedIn()
        }
    }

    fun launchLoginView()
    {
        Log.d("LOG", "${this::class.java} launching login fragment")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(LOGIN_FRAGMENT_ENTRY, loginfrag, LoginFragment.FRAGMENT_TAG)
        transaction.commit()
    }

    override fun onCreateUser(uid: String) {
        TODO("Not yet implemented")
    }

    /**
     * Food Entry stuffs
     */
    fun launchEntryView()
    {
        Log.d("LOG", "${this::class.java} Entry View launched")
        clearBackStack()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(ENTRY_FRAGMENT_ENTRY, entryFrag)
        transaction.commit()
        showMenuButtons()
    }

    override fun onEntryCreated() {
        setToolBarEntryView()
        showMenuButtons()
        setEntryViewDate()
        val createEntryBut = findViewById<Button>(R.id.addEntry)
        createEntryBut.setOnClickListener {
            launchCreateEntry()
        }
    }

    fun setEntryViewDate()
    {
        Log.d("LOG", "${this::class.java} Setting Entry View Main dates")
        // gets user selected date and initializes entry fragment data
        val userDate = app.dbHelper.getLoggedInSelectedDate()!!
        supportActionBar!!.setTitle(userDate)
        val list = app.getListOfEntries(userDate)
        entryFrag.initDiaryEntry(list, app.user!!)
    }


    //entry detailed implementation
    override fun hideEntryDetailed() {
        detachFragment(EntryFocussedDetailsFragment.FRAGMENT_TAG)
    }

    override fun showEntryDetails(entry: FoodDiaryEntry) {
        val frag = EntryFocussedDetailsFragment(entry, app.user!!)
        val tag = EntryFocussedDetailsFragment.FRAGMENT_TAG
        attachFragment(frag, tag)
    }

    //entry focussed implementation
    override fun showFoodEntryFocussed(entry: FoodDiaryEntry) {
        val focussedFrag = EntryFocussedFragment(entry, app.user!!)
        val tag = EntryFocussedFragment.FRAGMENT_TAG
        attachFragment(focussedFrag, tag)
    }

    override fun hideFoodEntryFocussed() {
        detachFragment(EntryFocussedFragment.FRAGMENT_TAG)
//        clearEntryFragSelection()
    }

    // launches view ingredients fragment
    override fun addViewIngredientsFragFromMainEntry(type: FoodType) {
        val middleMan = ViewIngredientEntryMiddleman(this, type)
        val viewIngFrag = ViewInredientsFragment(middleMan)
        viewIngFrag.type = type
        attachFragment(viewIngFrag, ViewInredientsFragment.FRAGMENT_TAG)
    }

    // override apater listener for view inrgredients lists
    override fun onDeleteFoodFromViewIng(name: String, ingFrag: ViewInredientsFragment) {
        val fm = supportFragmentManager
        intent.putExtra("name", name)
        val fragment = DeletePopupFrag(ingFrag.type)
        fragment.setTargetFragment(ingFrag, 1)
        fragment.show(fm, DeletePopupFrag.QTY_POPUP_FRAG_TAG)
    }

    override fun onAmmentFoodFromViewIng(name: String, ingFrag: ViewInredientsFragment) {
        val fm = supportFragmentManager
        intent.putExtra("name", name)
        when(ingFrag.type)
        {
            FoodType.INGREDIENT ->
            {
                val fragment = CreateIngFragment(app.dbHelper.getIngredient(name)!!)
                fragment.setTargetFragment(ingFrag, 1)
                attachFragment(fragment, CreateIngFragment.FRAGMENT_ID)
            }
            FoodType.RECIPE ->
            {
                val fragment = CreateRecipeFragment(app.dbHelper.getRecipe(name)!!)
                attachFragment(fragment, CreateRecipeFragment.FRAGMENT_ID)
            }
        }
    }


    /**
     * Calendar stuffs
     */
    private fun launchCalendarDialogueMainAct()
    {
        supportFragmentManager.popBackStack(CALENDAR_VIEW_TAG, 0)
        val date = app.dbHelper.getLoggedInSelectedDate()!!.split("/")
        val calDate = Calendar.getInstance()
        calDate.set(date.get(2).toInt(), date.get(1).toInt()-1, date.get(0).toInt())
        val timMilli = calDate.timeInMillis
        CalendarViewFragment(timMilli, this).show(supportFragmentManager, CalendarViewFragment.CALENDAR_FRAGMENT_TAG)
    }

    override fun onDateSet(year: Int, month: Int, day: Int) {
        val dateString = "$day/${month+1}/$year"
        calDateClickedEntry(dateString)
    }

    override fun calDateClickedEntry(date: String) {
        app.dbHelper.setLoggedInUser(app.user!!.uid, date)
        setEntryViewDate()
        clearEntryFragSelection()
        launchEntryView()
    }

    override fun onCalDismiss() {
        clearBackStack()
    }

    /**
     * Create Entry Stuffs
     */

    private fun launchCreateEntry()
    {
        supportActionBar?.title = CREATE_ENTRY_TITLE
        hideMenuButtons()
        attachFragment(CreateEntryFragment(), CreateEntryFragment.FRAGMENT_TAG)
    }

    override fun addViewIngredientsFragFromCreateEntry(type: FoodType, intf: ViewIngredientsListInterface) {
        val ingredientsFrag = ViewInredientsFragment(intf)
        when (type)
        {
            FoodType.INGREDIENT -> ingredientsFrag.type = FoodType.INGREDIENT
            FoodType.RECIPE -> ingredientsFrag.type = FoodType.RECIPE
        }
        attachFragment(ingredientsFrag, ViewInredientsFragment.FRAGMENT_TAG)
    }

    override fun addShowEntryViewFrag(list: List<Pair<FoodAccess, Float>>) {
        attachFragment(CreateEntryViewFragment(list), CreateEntryViewFragment.FRAGMENT_TAG)
    }

    /**
     * ViewIngredients fragment stuffs
     */
    //handles add ingredient button from viewingredientsinsertadapter
    override fun onInsertIngredient(name: String, ingFrag: ViewInredientsFragment) {
        val fm = supportFragmentManager
        val fragment = QuantityPopupFrag()
        fragment.setTargetFragment(ingFrag, 1)
        intent.putExtra("name", name)
        val ingRec: FoodAccess
        when(ingFrag.type)
        {
            FoodType.INGREDIENT -> ingRec = app.dbHelper.getIngredient(name)!!
            FoodType.RECIPE -> ingRec = app.dbHelper.getRecipe(name)!!
        }
        intent.putExtra("defQty", ingRec.whatServing())
        fragment.show(fm, QuantityPopupFrag.QTY_POPUP_FRAG_TAG)
    }

    /**
     * CreateEntryViewFrag stuffs
     */

    override fun onEntryCreated(frag: CreateEntryViewFragment) {
        frag.initDiaryEntry(app.user!!)
    }

    override fun onEntryViewFinish(list: List<Pair<FoodAccess, Float>>) {
        attachFragment(CreateEntryFinalFragment(list), CreateEntryFinalFragment.FRAGMENT_TAG)
    }

    // food entry focussed stuffs
    override fun showFoodEntryFocussed(entry: Pair<FoodAccess, Float>) {
        val foodEntry = FoodDiaryEntry()
        when(entry.first.whatType())
        {
            FoodType.RECIPE -> foodEntry.addRecipe(entry.first as Recipe, entry.second)
            FoodType.INGREDIENT -> foodEntry.addIngredient(entry.first as Ingredient, entry.second)
        }
        val frag = EntryFocussedFragment(foodEntry, app.user!!, false)
        attachFragment(frag, EntryFocussedFragment.FRAGMENT_TAG)
    }

    override fun hideFoodEntryFocussed(frag: CreateEntryViewFragment) {
        frag.clearSelection()
        detachFragment(EntryFocussedFragment.FRAGMENT_TAG)
    }

    // final frag stuffs
    override fun onLaunchCalendarFromCreateFinal(timeMilli: Long, frag: CreateEntryFinalFragment) {
        CalendarViewFragment(timeMilli, frag)
    }

    override fun onLaunchTimeFromCreateFinal(timeMilli: Long, frag: CreateEntryFinalFragment) {
        TimeDialoueFragment(timeMilli, frag)
    }

    override fun onFinish() {
        clearBackStack()
        showMenuButtons()
        setEntryViewDate()
    }

    // launch create ingredient/recipe from create entry view
    override fun onCreateIngredient() {
        attachFragment(CreateIngFragment(), CreateIngFragment.FRAGMENT_ID)
        supportActionBar?.title = CREATE_ING_TITLE
    }

    override fun onCreateRecipe() {
        attachFragment(CreateRecipeFragment(), CreateRecipeFragment.FRAGMENT_ID)
        supportActionBar?.title = CREATE_REC_TITLE
    }


    /**
     * Create Recipe stuffs
     */
    override fun onRecipeAddIngredient(intf: ViewIngredientsListInterface) {
        val ingredientsFrag = ViewInredientsFragment(intf)
        ingredientsFrag.type = FoodType.INGREDIENT
        attachFragment(ingredientsFrag, ViewInredientsFragment.FRAGMENT_TAG)
    }

    override fun onRecipeFinalize() {
        if (isTopFragStack(CreateRecipeDetailsFragment.FRAGMENT_TAG))
            onBackPressed()
        onBackPressed()
    }

    override fun onCreateRecipeMoreDetails(ingList: MutableList<Pair<FoodAccess, Float>>) {
        attachFragment(CreateRecipeDetailsFragment(ingList), CreateRecipeDetailsFragment.FRAGMENT_TAG)
    }

    /**
     * Create Ing fra stuffs
     */
    override fun onCreateIngredient(ing: Ingredient): Boolean {
        return app.dbHelper.insertIngredient(ing)
    }

    override fun onIngredientCreated() {
        onBackPressed()
    }


    private fun isTopFragStack(tag: String): Boolean
    {
        val backStackCount = supportFragmentManager.backStackEntryCount
        if (backStackCount <=0 )
            return false
        var fragTopStackTag =
            supportFragmentManager.getBackStackEntryAt(backStackCount - 1)
                .name
        return fragTopStackTag.equals(tag)
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}