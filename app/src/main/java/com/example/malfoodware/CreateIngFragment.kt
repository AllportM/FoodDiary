package com.example.malfoodware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.create_ingredient_fragment.*

class CreateIngFragment: Fragment() {

    companion object
    {
        val FRAGMENT_ID = "createFoodFragment"
    }

    interface CreateInredientActivityListener
    {
        fun onCreateIngredient(ing: Ingredient): Boolean
        fun onIngredientCreated()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.create_ingredient_fragment, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("LOG", "${this::class.java} view created")
        super.onViewCreated(view, savedInstanceState)
        createIngBackBut.setOnClickListener {
            requireActivity().onBackPressed()
        }
        viewIngredientFinalize.setOnClickListener {
            if(checkInputValid()) {
                if (ingredientExists()) {
                    createIngNameErrorDuplicate.visibility = TextView.VISIBLE
                }
                else {
                    Log.d("LOG", "${this::class.java} ingredient created")
                    activityApp.onIngredientCreated()
                }
            }
        }
        setFocusListeners()
    }

    lateinit var activityApp: CreateInredientActivityListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateInredientActivityListener)
            activityApp = context
        else
            Log.d("LOG", "${this::class} error attaching activity listener")
    }

    private fun setFocusListeners()
    {
        createIngCarbsVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        createIngEnergyVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        createIngFatVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        createIngFibreVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        createIngNameVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        createIngProteinVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        createIngSaltVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        createIngServingVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
    }

    private fun ingredientExists(): Boolean
    {
        val carbs = createIngCarbsVal.text.toString().toFloat()
        val energy = createIngEnergyVal.text.toString().toFloat()
        val protein = createIngProteinVal.text.toString().toFloat()
        val salt = createIngSaltVal.text.toString().toFloat()
        val fat = createIngFatVal.text.toString().toFloat()
        val fibre = createIngFibreVal.text.toString().toFloat()
        val serving = createIngServingVal.text.toString().toFloat()
        val name = createIngNameVal.text.toString()
        val ingredient = Ingredient(name, energy, fat, carbs, fibre, protein, salt, serving)
        Log.d("LOG", "${this::class.java} checking if ingredient exists with ing: $ingredient")
        return !activityApp.onCreateIngredient(ingredient)
    }

    private fun checkInputValid(): Boolean
    {
        hideErrors()
        Log.d("LOG", "${this::class.java} checking input validity")
        var valid = true
        if (createIngCarbsVal.text.toString().equals(""))
        {
            createIngCarbError.visibility = TextView.VISIBLE
            valid = false
        }
        if (createIngEnergyVal.text.toString().equals(""))
        {
            createIngEnergyErr.visibility = TextView.VISIBLE
            valid = false
        }
        if (createIngProteinVal.text.toString().equals(""))
        {
            createIngProteinErr.visibility = TextView.VISIBLE
            valid = false
        }
        if (createIngFatVal.text.toString().equals(""))
        {
            createIngFatErr.visibility = TextView.VISIBLE
            valid = false
        }
        if (createIngFibreVal.text.toString().equals(""))
        {
            createIngFribreErr.visibility = TextView.VISIBLE
            valid = false
        }
        if (createIngSaltVal.text.toString().equals(""))
        {
            createIngSaltErr.visibility = TextView.VISIBLE
            valid = false
        }
        if (createIngServingVal.text.toString().equals(""))
        {
            createIngServingErr.visibility = TextView.VISIBLE
            valid = false
        }
        else if (createIngServingVal.text.toString().toFloat() == 0f)
        {
            createIngServingErrZero.visibility = TextView.VISIBLE
            valid = false
        }
        if (createIngNameVal.text.toString().equals(""))
        {
            createIngNameErrorEmpty.visibility = TextView.VISIBLE
            valid = false
        }
        return valid
    }

    private fun hideErrors()
    {
        Log.d("LOG", "${this::class.java} hiding input errors")
        createIngCarbError.visibility = TextView.INVISIBLE
        createIngEnergyErr.visibility = TextView.INVISIBLE
        createIngFatErr.visibility = TextView.INVISIBLE
        createIngFribreErr.visibility = TextView.INVISIBLE
        createIngProteinErr.visibility = TextView.INVISIBLE
        createIngSaltErr.visibility = TextView.INVISIBLE
        createIngServingErr.visibility = TextView.INVISIBLE
        createIngNameErrorDuplicate.visibility = TextView.INVISIBLE
        createIngNameErrorEmpty.visibility = TextView.INVISIBLE
        createIngServingErrZero.visibility = TextView.INVISIBLE
    }
}