package com.example.malfoodware

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.setPadding
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var app: App
    lateinit var ACTIVITY_MANAGER: ViewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ACTIVITY_MANAGER = ViewManager(this)
        app = App(applicationContext)
        ACTIVITY_MANAGER.setView(Views.MAIN_VIEW)

        var uidText = findViewById<TextView>(R.id.unameInput)
        // sets to hide keyboard when focus lost
        uidText.setOnFocusChangeListener {
                view, b -> if(!b) view.hideKeyboard()
        }

        var loginBut = findViewById<TextView>(R.id.loginBut)
        var createUserBut = findViewById<TextView>(R.id.createUser)
        loginBut.setTransformationMethod(null)
        createUserBut.setTransformationMethod(null)
        loginBut.setOnClickListener { view ->
            var name: String = uidText.text.toString()
            if (!app.login(name))
                Toast.makeText(this, "User not found, check log", Toast.LENGTH_SHORT).show()
            else
            {
                ACTIVITY_MANAGER.setView(Views.ENTRY_VIEW)
            }
        }
    }

    fun initDiaryEntry(date: String)
    {
        var entries = app.dbHelper.getDiaryEntriesDate(app.user!!.uid, date)
        setDiaryViewWheels(entries)
        setDiaryViewList(entries)
    }

    @SuppressLint("ResourceAsColor")
    fun setDiaryViewList(entries: SortedSet<FoodDiaryEntry>)
    {
        val table = findViewById<TableLayout>(R.id.tableFoodEntries)
        table.isStretchAllColumns = true
        table.setBackgroundColor(R.color.purple_hover)

        for (entry in entries)
        {
            for (i in 0 until 5) {
                val row = TableRow(this)
                row.setBackgroundColor(R.color.deep_purple)
                row.gravity = Gravity.CENTER
                row.setPadding(50)
                val bloodSugar = TextView(this)
                bloodSugar.text = "${entry.bloodSugar}"
                val foods = TextView(this)
                var foodsText = ""
                for (recipe in entry.recipes) {
                    foodsText += recipe.key.recName + "\n"
                }
                for (ingredient in entry.ingredients) {
                    foodsText += ingredient.key.name + "\n"
                }
                if (foodsText[foodsText.length - 1] == '\n')
                    foodsText = foodsText.substring(0, foodsText.length - 1)
                foods.text = foodsText
                val time = TextView(this)
                val cal = Calendar.getInstance()
                cal.timeInMillis = entry.timeMillis
                val hourString =
                    if (cal.get(Calendar.HOUR) < 10) "0" + cal.get(Calendar.HOUR).toString()
                    else cal.get(Calendar.HOUR).toString()
                val minuteString =
                    if (cal.get(Calendar.MINUTE) < 10) "0" + cal.get(Calendar.MINUTE).toString()
                    else cal.get(Calendar.MINUTE).toString()
                val timeString = hourString + ":" + minuteString
                time.text = timeString
                bloodSugar.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                foods.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                time.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            bloodSugar.gravity = Gravity.CENTER
//            foods.gravity = Gravity.LEFT
//            time.gravity = Gravity.CENTER
                row.addView(bloodSugar)
                row.addView(foods)
                row.addView(time)
                row.isClickable = true
                row.setOnClickListener { println("HEY") }
                table.addView(row)
            }
        }
    }

    fun setDiaryViewWheels(entries: SortedSet<FoodDiaryEntry>)
    {
        val progProtein = findViewById<ProgressBar>(R.id.progressBarProtein)
        val progCarbs = findViewById<ProgressBar>(R.id.progressBarCarbs)
        val progEnergy = findViewById<ProgressBar>(R.id.progressBarEnergy)
        val progFat = findViewById<ProgressBar>(R.id.progressBarFat)
        val progSalt = findViewById<ProgressBar>(R.id.progressBarSalt)
        val progFribre = findViewById<ProgressBar>(R.id.progressBarFibre)
        val qtyProtein = findViewById<TextView>(R.id.textViewProteinQty)
        val qtyCarbs = findViewById<TextView>(R.id.textViewCarbsQty)
        val qtyEnergy = findViewById<TextView>(R.id.textViewEnergyQty)
        val qtyFat = findViewById<TextView>(R.id.textViewFatQty)
        val qtySalt = findViewById<TextView>(R.id.textViewSaltQty)
        val qtyFibre = findViewById<TextView>(R.id.textViewFibreQty)
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
                    else
                        nutrition.plusAssign(recipe.key.getNutrition()!! / recipe.value)
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
        qtyCarbs.text = "${nutrition.carbs.toInt()} / ${app.user!!.nutritionPerDay.carbs.toInt()}"
        qtyEnergy.text = "${nutrition.energy.toInt()} / ${app.user!!.nutritionPerDay.energy.toInt()}"
        qtyFat.text = "${nutrition.fat.toInt()} / ${app.user!!.nutritionPerDay.fat.toInt()}"
        qtyFibre.text = "${nutrition.fibre.toInt()} / ${app.user!!.nutritionPerDay.fibre.toInt()}"
        qtyProtein.text = "${nutrition.protein.toInt()} / ${app.user!!.nutritionPerDay.protein.toInt()}"
        qtySalt.text = "${nutrition.salt.toInt()} / ${app.user!!.nutritionPerDay.salt.toInt()}"
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onPause() {
        super.onPause()
        app.dbHelper.setLoggedInUser(app.user?.uid)
    }

    override fun onResume() {
        super.onResume()
        val uid = app.dbHelper.getLoggedInUser()
        if (uid != null)
        {
            app.login(uid)
            ACTIVITY_MANAGER.setView(Views.ENTRY_VIEW)
            initDiaryEntry("2/7/2020")
        }
    }

}

enum class Views
{
    MAIN_VIEW,
    ENTRY_VIEW
}
class ViewManager(val activity: AppCompatActivity)
{
    fun setView(view: Views)
    {
        when(view)
        {
            Views.MAIN_VIEW -> activity.setContentView(R.layout.activity_main)
            Views.ENTRY_VIEW -> activity.setContentView(R.layout.diary_entry_view)
        }
    }
}