package com.example.malfoodware

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.create_recipe_nutrition_view.*
import kotlin.math.roundToInt

class CreateRecipeDetailsFragment(val ingList: MutableList<Pair<FoodAccess, Float>>):
        Fragment()
{

    companion object {
        val FRAGMENT_TAG = "createRecipeDetailsFrag"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.create_recipe_nutrition_view, container, false)
        return v
    }

    // init variables
    // initilaizes ViewTexts and sets the text values for given entry
    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LOG", "${this::class.java} view created")
        //setup main elemtns
        setValues()
        createRecLessDetails.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        setValues()
    }

    private fun setValues()
    {
        var nutrition: Nutrition? = null
        if (ingList.size > 0)
        {
            var entryIterator = ingList.iterator()
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
        val progProtein = createRecipeProgressBarProtein
        val progCarbs = createRecipeProgressBarCarbs
        val progEnergy = createRecipeProgressBarEnergy
        val progFat = createRecProgressBarFat
        val progSalt = createRecipeProgressBarSalt
        val progFribre = createRecipeProgressBarFibre
        val qtyProtein = createRecipeTextViewProteinQty
        val qtyCarbs = createRecipeTextViewCarbsQty
        val qtyEnergy = createRecipeTextViewEnergyQty
        val qtyFat = createRecipeTextViewFatQty
        val qtySalt = createRecipeTextViewSaltQty
        val qtyFibre = createRecipeTextViewFibreQty
        val user = (requireActivity() as MainActivity).app.user!!
        progProtein.progress =
            Math.round(nutrition!!.protein / user.nutritionPerDay.protein * 100)
        progCarbs.progress =
            Math.round(nutrition.carbs / user.nutritionPerDay.carbs * 100)
        progEnergy.progress =
            Math.round(nutrition.energy / user.nutritionPerDay.energy * 100)
        progFat.progress =
            Math.round(nutrition.fat / user.nutritionPerDay.fat * 100)
        progFribre.progress =
            Math.round(nutrition.fibre / user.nutritionPerDay.fibre * 100)
        progSalt.progress =
            Math.round(nutrition.salt / user.nutritionPerDay.salt * 100)
        qtyCarbs.text = "${nutrition.carbs.roundToInt()}g"
        qtyEnergy.text = "${nutrition.energy.roundToInt()}g"
        qtyFat.text = "${nutrition.fat.round(1)}g"
        qtyFibre.text = "${nutrition.fibre.round(1)}g"
        qtyProtein.text = "${nutrition.protein.round(1)}g"
        qtySalt.text = "${nutrition.salt.round(2)}g"
    }

}