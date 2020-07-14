package com.example.malfoodware

import FoodEntriesAdapter
import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var ACTIVITY_MANAGER: ViewManager
    lateinit var app:  App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ACTIVITY_MANAGER = ViewManager(this)
        app = App(applicationContext)
        ACTIVITY_MANAGER.setView(Views.MAIN_VIEW)
        setSupportActionBar(findViewById(R.id.toolbar2))
        var uidText = findViewById<TextView>(R.id.unameInput)
        // sets to hide keyboard when focus lost
        uidText.setOnFocusChangeListener {
                view, b -> if(!b) view.hideKeyboard()
        }

        var loginBut = findViewById<TextView>(R.id.loginBut)
        var createUserBut = findViewById<TextView>(R.id.createUser)
        loginBut.setTransformationMethod(null)
        createUserBut.setTransformationMethod(null)
        loginBut.setOnClickListener {
            var name: String = uidText.text.toString()
            if (!app.login(name))
                Toast.makeText(this, "User not found, check log", Toast.LENGTH_SHORT).show()
            else
            {
                val date = Calendar.getInstance()
                val dayString = date.get(Calendar.DAY_OF_MONTH)
                val monthString = date.get(Calendar.MONTH)
                val yearString = date.get(Calendar.YEAR)
                var title = "$dayString/$monthString/$yearString"
                app.dbHelper.setLoggedInUser(app.user!!.uid, title)
                ACTIVITY_MANAGER.setView(Views.ENTRY_POINT)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        val uid = app.dbHelper.getLoggedInUser()
        if (uid != null)
        {
            ACTIVITY_MANAGER.setView(Views.ENTRY_POINT)
        }
    }
}

enum class Views
{
    MAIN_VIEW,
    ENTRY_VIEW,
    ENTRY_POINT
}
class ViewManager(val activity: AppCompatActivity)
{
    fun setView(view: Views)
    {
        when(view)
        {
            Views.MAIN_VIEW -> activity.setContentView(R.layout.activity_main)
            Views.ENTRY_VIEW -> activity.setContentView(R.layout.diary_entry_view)
            Views.ENTRY_POINT -> activity.setContentView(R.layout.entry_point)
        }
    }
}