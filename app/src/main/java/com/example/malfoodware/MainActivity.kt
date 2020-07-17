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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.*

class MainActivity :
        AppCompatActivity(),
        LoginFragment.LoginFragmentListener,
        EntryFragmentMain.EntryViewListener,
        CalendarViewFragment.DatePickerListener,
        EntriesAdapter.
        FoodEntryListListener,
        EntryFocussedDetailsFragment.EntryDetailsListener,
        EntryFocussedFragment.EntryFocussedListener
{
    lateinit var app:  App
    private val LOGIN_FRAGMENT_ENTRY = R.id.fragmentEntryPoint
    private val ENTRY_FRAGMENT_ENTRY = R.id.entryFragmentEntry
    private val CALENDAR_VIEW_TAG = "calendarView"
    lateinit var loginfrag: LoginFragment
    lateinit var entryFrag: EntryFragmentMain

    companion object {
        val CALENDAR_TYPE_ENTRY = 1
        val CALENDAR_TYPE_FROM = 2
        val CALENDAR_TYPE_TO = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LOG", "Main activity created")
        app = App(applicationContext)
        setContentView(R.layout.content_main)
        setSupportActionBar(findViewById(R.id.toolbar2))
        loginfrag = LoginFragment()
        entryFrag = EntryFragmentMain()
        checkUserSignedIn()
    }

    fun checkUserSignedIn()
    {
        val uid = app.dbHelper.getLoggedInUser()
        if (uid != null)
        {
            Log.d("LOG", "Existing user found, logging in")
            app.login(uid)
            setContentView(R.layout.my_toolbar)
            launchEntryView()
        }
        else
            launchLoginView()
    }

    fun clearBackStack()
    {
        entryFrag.clearSelection()
        for (fragment in supportFragmentManager.fragments)
        {
            Log.d("LOG", "Fragment popped")
            supportFragmentManager.popBackStack()
        }
    }

    fun setToolBar()
    {
        val toolbar: Toolbar = findViewById(R.id.toolbar3)
        setSupportActionBar(toolbar)
        var calendarButton = findViewById<Button>(R.id.toolbar_calendar)
//        setActionBar(toolbar)
        calendarButton.setOnClickListener {
            Log.d("LOG", "Calendar button clicked")
            launchCalendarDialogue()
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
//        checkUserSignedIn()
    }

    fun attachFragment(fragment: Fragment, tag: String)
    {
        var transaction = supportFragmentManager.beginTransaction()
        transaction.add(ENTRY_FRAGMENT_ENTRY, fragment, tag)
            .addToBackStack(tag)
        transaction.show(fragment)
        transaction.commit()
    }

    fun detachFragment(tag: String)
    {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.hide(fragment)
            transaction.remove(fragment)
            transaction.commit()
            supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    /**
     * Login Stuffs
     */
    override fun onLogin(uid: String) {
        if (!app.login(uid))
            Toast.makeText(this, "Failed to login with $uid, user already exists",
                Toast.LENGTH_SHORT)
        else
        {
            val date = Calendar.getInstance()
            val dayString = date.get(Calendar.DAY_OF_MONTH)
            val monthString = date.get(Calendar.MONTH) - 1
            val yearString = date.get(Calendar.YEAR)
            val dateString = "$dayString/$monthString/$yearString"
            app.dbHelper.setLoggedInUser(uid, dateString)
            checkUserSignedIn()
        }
    }


    fun launchLoginView()
    {
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
    override fun onEntryCreated() {
        setEntryViewDate()
        setToolBar()
    }

    fun setEntryViewDate()
    {
        Log.d("LOG", "Setting Entry View Main dates")
        // gets user selected date and initializes entry fragment data
        val userDate = app.dbHelper.getLoggedInSelectedDate()!!
        supportActionBar!!.setTitle(userDate)
        val list = app.getListOfEntries(userDate)
        for (entry in list)
        {
            println(entry.recipes)
        }
        entryFrag.initDiaryEntry(list, app.user!!)
    }

    fun launchEntryView()
    {
        Log.d("LOG", "Entry View launched")
        clearBackStack()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(ENTRY_FRAGMENT_ENTRY, entryFrag)
        transaction.commit()
        setToolBar()
    }

    //Fod entry detailed implementation
    override fun hideEntryDetailed() {
        detachFragment(EntryFocussedDetailsFragment.FRAGMENT_TAG)
    }
    override fun showEntryDetails(entry: FoodDiaryEntry) {
        val frag = EntryFocussedDetailsFragment(entry, app.user!!)
        val tag = EntryFocussedDetailsFragment.FRAGMENT_TAG
        attachFragment(frag, tag)
    }

    //Food entry focussed implementation
    override fun showFoodEntryFocussed(entry: FoodDiaryEntry) {
        val focussedFrag = EntryFocussedFragment(entry, app.user!!)
        val tag = EntryFocussedDetailsFragment.FRAGMENT_TAG
        attachFragment(focussedFrag, tag)
    }

    override fun hideFoodEntryFocussed() {
        detachFragment(EntryFocussedDetailsFragment.FRAGMENT_TAG)
    }

    /**
     * Calendar stuffs
     */
    fun launchCalendarDialogue()
    {
        supportFragmentManager.popBackStack(CALENDAR_VIEW_TAG, 0)
        val date = app.dbHelper.getLoggedInSelectedDate()!!.split("/")
        val calDate = Calendar.getInstance()
        calDate.set(date.get(2).toInt(), date.get(1).toInt()-1, date.get(0).toInt())
        val timMilli = calDate.timeInMillis
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(CalendarViewFragment(CALENDAR_TYPE_ENTRY, timMilli),
            CalendarViewFragment.CALENDAR_FRAGMENT_TAG).addToBackStack(CALENDAR_VIEW_TAG)
        transaction.commit()
    }
    override fun calDateClickedEntry(date: String) {
        app.dbHelper.setLoggedInUser(app.user!!.uid, date)
        setEntryViewDate()
        launchEntryView()
    }

    override fun calDateClickedFrom(date: String) {
        TODO("Not yet implemented")
    }

    override fun calDateClickedTo(date: String) {
        TODO("Not yet implemented")
    }

    override fun onCalDismiss() {
        clearBackStack()
    }


}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}