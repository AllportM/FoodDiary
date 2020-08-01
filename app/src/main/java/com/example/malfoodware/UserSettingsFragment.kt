package com.example.malfoodware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.user_settings.*

class UserSettingsFragment(val user: User): Fragment() {

    companion object
    {
        val FRAGMENT_ID = "userSettingsFragment"
    }

    interface CreateInredientActivityListener
    {
        fun onSaveUserSettings(use: User)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.user_settings, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("LOG", "${this::class.java} view created")
        super.onViewCreated(view, savedInstanceState)
        userSetBackBut.setOnClickListener {
            requireActivity().onBackPressed()
        }
        userSetSave.setOnClickListener {
            if(checkInputValid()) {
                val user = createUser()
                Log.d("LOG", "${this::class.java} saving user ${user}")
                activityApp.onSaveUserSettings(user)
            }
        }
        setFocusListeners()
        setValues()
    }

    lateinit var activityApp: CreateInredientActivityListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateInredientActivityListener)
            activityApp = context
        else
            Log.d("LOG", "${this::class} error attaching activity listener")
    }

    private fun setValues()
    {
        userSetCarbsVal.setText(user.nutritionPerDay.carbs.toString())
        userSetEnergyVal.setText(user.nutritionPerDay.energy.toString())
        userSetProteinValue.setText(user.nutritionPerDay.protein.toString())
        userSetSaltValue.setText(user.nutritionPerDay.salt.toString())
        userSetFatVal.setText(user.nutritionPerDay.fat.toString())
        userSetFibreVal.setText(user.nutritionPerDay.fibre.toString())
        userSetInsVal.setText(user.INSULIN_PER10G.toString())
        userSetText.setText("User Settings: ${user.uid}")
    }

    private fun setFocusListeners()
    {
        userSetCarbsVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        userSetEnergyVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        userSetFatVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        userSetFibreVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        userSetProteinValue.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        userSetSaltValue.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        userSetInsVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
    }

    private fun createUser(): User
    {
        val carbs = userSetCarbsVal.text.toString().toFloat()
        val energy = userSetEnergyVal.text.toString().toFloat()
        val protein = userSetProteinValue.text.toString().toFloat()
        val salt = userSetSaltValue.text.toString().toFloat()
        val fat = userSetFatVal.text.toString().toFloat()
        val fibre = userSetFibreVal.text.toString().toFloat()
        val ins = userSetInsVal.text.toString().toFloat()
        return User(user.uid, ins, Nutrition(energy, fat, carbs, fibre, protein, salt))
    }

    private fun checkInputValid(): Boolean
    {
        hideErrors()
        Log.d("LOG", "${this::class.java} checking input validity")
        var valid = true
        if (userSetCarbsVal.text.toString().equals(""))
        {
            userSetCarbError.visibility = TextView.VISIBLE
            valid = false
        }
        else if (userSetCarbsVal.text.toString().toFloat() >= 1000)
        {
            userSetCarbErrorRealistic.visibility = TextView.VISIBLE
            valid = false
        }
        if (userSetEnergyVal.text.toString().equals(""))
        {
            userSetEnergyErr.visibility = TextView.VISIBLE
            valid = false
        }
        else if (userSetEnergyVal.text.toString().toFloat() >= 5000)
        {
            userSetEnergyErrRealistic.visibility = TextView.VISIBLE
            valid = false
        }
        if (userSetProteinValue.text.toString().equals(""))
        {
            userSetProteinErrorRealistic.visibility = TextView.VISIBLE
            valid = false
        }
        else if (userSetProteinValue.text.toString().toFloat() >= 5000)
        {
            userSetProteinErrorRealistic.visibility = TextView.VISIBLE
            valid = false
        }
        if (userSetFatVal.text.toString().equals(""))
        {
            userSetFatError.visibility = TextView.VISIBLE
            valid = false
        }
        else if (userSetFatVal.text.toString().toFloat() >= 200)
        {
            userSetFatErrorRealistic.visibility = TextView.VISIBLE
            valid = false
        }
        if (userSetFibreVal.text.toString().equals(""))
        {
            userSetFibreError.visibility = TextView.VISIBLE
            valid = false
        }
        else if (userSetFibreVal.text.toString().toFloat() >= 200)
        {
            userSetFibreErrorRealistic.visibility = TextView.VISIBLE
            valid  = false
        }
        if (userSetSaltValue.text.toString().equals(""))
        {
            userSetSaltError.visibility = TextView.VISIBLE
            valid = false
        }
        else if (userSetSaltValue.text.toString().toFloat() >= 50)
        {
            userSetSaltErrorRealistic.visibility = TextView.VISIBLE
            valid = false
        }
        if (userSetInsVal.text.toString().equals(""))
        {
            userSerInsulinError.visibility = TextView.VISIBLE
            valid = false
        }
        return valid
    }

    private fun hideErrors()
    {
        Log.d("LOG", "${this::class.java} hiding input errors")
        userSetCarbError.visibility = TextView.INVISIBLE
        userSetEnergyErr.visibility = TextView.INVISIBLE
        userSetFatError.visibility = TextView.INVISIBLE
        userSetFibreError.visibility = TextView.INVISIBLE
        userSetProteinErrorRealistic.visibility = TextView.INVISIBLE
        userSetSaltError.visibility = TextView.INVISIBLE
        userSerInsulinError.visibility = TextView.INVISIBLE
        userSetCarbErrorRealistic.visibility = TextView.INVISIBLE
        userSetEnergyErrRealistic.visibility = TextView.INVISIBLE
        userSetFatErrorRealistic.visibility = TextView.INVISIBLE
        userSetFibreErrorRealistic.visibility = TextView.INVISIBLE
        userSetProteinErrorRealistic.visibility = TextView.INVISIBLE
        userSetSaltErrorRealistic.visibility = TextView.INVISIBLE
    }
}