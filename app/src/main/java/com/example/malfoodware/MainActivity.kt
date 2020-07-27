package com.example.malfoodware

import EntriesAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.diary_entry_view.*
import java.security.KeyStore
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
        var COL_LIST_LIGHT = 0
        var COL_LIST_DARK = 0
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
        COL_LIST_LIGHT = ResourcesCompat.getColor(resources, R.color.list_light_grey,null)
        COL_LIST_DARK = ResourcesCompat.getColor(resources, R.color.list_dark_grey, null)
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
        for (fragment in supportFragmentManager.fragments)
        {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOG", "Main Activity Destroyed")
    }

    override fun onBackPressed() {
        val backStackCount = supportFragmentManager.backStackEntryCount
        if (backStackCount > 0) {
            var fragTopStackTag =
                supportFragmentManager.getBackStackEntryAt(backStackCount - 1)
                    .name
            if (fragTopStackTag.equals(EntryFocussedFragment.FRAGMENT_TAG)) {
                clearEntryFragSelection()
            }
            supportFragmentManager.popBackStack()
        }
        else
            super.onBackPressed()
    }

    fun attachFragment(fragment: Fragment, tag: String)
    {
        var transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
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
            transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            transaction.remove(fragment)
            transaction.commit()
            supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun clearEntryFragSelection()
    {
        EntryFocussedFragment.LAST_ENTRY = FoodDiaryEntry(1)
        entryFrag.clearSelection()
        val adapter = entryFrag.rvEntries?.adapter as EntriesAdapter
        adapter.clicked = null
    }

    /**
     * Login Stuffs
     */
    override fun onLogin(uid: String) {
        if (!app.login(uid)) {
            Log.d("LOG", "${this::class} failed to login with uid: $uid")
            Toast.makeText(
                this, "Failed to login with $uid, user already exists",
                Toast.LENGTH_SHORT
            )
        }
        else
        {
            Log.d("LOG", "${this::class} logging in with uid: $uid")
            setUserClickedDateToday()
            checkUserSignedIn()
        }
    }

    fun setUserClickedDateToday()
    {
        val date = Calendar.getInstance()
        val dayString = date.get(Calendar.DAY_OF_MONTH)
        val monthString = date.get(Calendar.MONTH) + 1
        val yearString = date.get(Calendar.YEAR)
        val dateString = "$dayString/$monthString/$yearString"
        app.dbHelper.setLoggedInUser(app.user!!.uid, dateString)
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
        setToolBar()
        setEntryViewDate()
        val createEntryBut = findViewById<Button>(R.id.addEntry)
        createEntryBut.setOnClickListener {
            val intent = Intent(this, CreateEntryActivity::class.java)
            intent.putExtra("UNAME", app.user!!.uid)
            startActivity(intent)
        }
    }

    fun setEntryViewDate()
    {
        Log.d("LOG", "Setting Entry View Main dates")
        // gets user selected date and initializes entry fragment data
        val userDate = app.dbHelper.getLoggedInSelectedDate()!!
        supportActionBar!!.setTitle(userDate)
        val list = app.getListOfEntries(userDate)
        entryFrag.initDiaryEntry(list, app.user!!)
    }

    fun launchEntryView()
    {
        Log.d("LOG", "Entry View launched")
        clearBackStack()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(ENTRY_FRAGMENT_ENTRY, entryFrag)
        transaction.commit()
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
        clearEntryFragSelection()
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
        transaction.add(CalendarViewFragment(timMilli),
            CalendarViewFragment.CALENDAR_FRAGMENT_TAG).addToBackStack(CALENDAR_VIEW_TAG)
        transaction.commit()
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


}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}