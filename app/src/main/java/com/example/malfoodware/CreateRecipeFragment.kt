package com.example.malfoodware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.create_recipe_fragment.*
import java.util.*

class CreateRecipeFragment(): Fragment(), ViewIngredientsListInterface {

    constructor(recipe: Recipe): this()
    {
        this.recipe = recipe
    }

    var recipe: Recipe? = null
    lateinit var activityApp: CreateRecipeActivityListener
    var ingList = mutableListOf<Pair<FoodAccess, Float>>()
    lateinit var rvEntries: RecyclerView
    var finishClicked = false

    companion object
    {
        val FRAGMENT_ID = "createRecipeFragment"
        val BAR_TITLE = "Create Recipe"
    }

    interface CreateRecipeActivityListener
    {
        fun onRecipeAddIngredient(intf: ViewIngredientsListInterface)
        fun onRecipeFinalize()
        fun onCreateRecipeMoreDetails(ingList: MutableList<Pair<FoodAccess, Float>>)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("LOG", "${this::class.java} view created")
        val v = inflater.inflate(R.layout.create_recipe_fragment, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        Log.d("LOG", "${this::class.java} view created")
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setListAndAdapter()
    }

    private fun setListAndAdapter()
    {
        // if not null then ammending recipe
        if (recipe != null)
        {
            for (ing in recipe!!.ingList)
            {
                ingList.add(Pair(ing.key, ing.value))
            }
            createRecipeNameVal.setText(recipe!!.recName)
            createRecipePortionQty.setText(recipe!!.portion.toString())
        }
        rvEntries = createRecipeInList
        rvEntries.layoutManager = LinearLayoutManager(context)
        rvEntries.adapter = CreateRecipeAdapter(ingList, this)
        setMoreDetailsButtonVisible()
    }

    private fun setListeners()
    {
        createRecipeAddIngredient.setOnClickListener {
            activityApp.onRecipeAddIngredient(this)
        }
        createRecipeFinishBut.setOnClickListener{
            finishClicked = true
            if (checkInputValid())
            {
                // inserting ingredient
                if (recipe == null)
                {
                    if (recipeExists())
                    {
                        Log.d(
                            "LOG",
                            "${this::class.java} failed to insert recipe - duplicate found"
                        )
                        createRecipeErrDuplicate.visibility = TextView.VISIBLE
                        (requireActivity() as MainActivity).detachFragment(
                            CreateRecipeDetailsFragment.FRAGMENT_TAG
                        )
                    }
                    else
                    {
                        Log.d("LOG", "${this::class.java} recipe inserted, closing recipe fragment")
                        activityApp.onRecipeFinalize()
                    }
                }
                // are ammendin ingredient
                else
                {
                    val recipe = createRecipe()
                    val activity = requireActivity() as MainActivity
                    if (activity.app.dbHelper.ammendRecipe(this.recipe!!, recipe))
                    {
                        Log.d("LOG", "${this::class.java} successfully ammended recipe")
                    }
                    else
                    {
                        Log.d(
                            "LOG", "${this::class.java} failed to ammend recipe\nold recipe: " +
                                    "${this.recipe}\nnew recipe: ${this::recipe}"
                        )
                    }
                    activityApp.onRecipeFinalize()
                }
            }
            else
            {
                (requireActivity() as MainActivity).detachFragment(
                    CreateRecipeDetailsFragment.FRAGMENT_TAG
                )
            }
        }
        createRecBackBut.setOnClickListener {
            requireActivity().onBackPressed()
        }
        createRecipeNameVal.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        createRecipePortionQty.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
    }

    private fun setMoreDetailsButtonVisible()
    {
        if (ingList.size > 0)
            createRecipeMoreDetails.visibility = TextView.VISIBLE
        else
            createRecipeMoreDetails.visibility = TextView.INVISIBLE
        createRecipeMoreDetails.setOnClickListener {
            activityApp.onCreateRecipeMoreDetails(ingList)
        }
    }

    override fun onResume() {
        Log.d("LOG", "${this::class.java} resumed")
        super.onResume()
    }


    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        if (context is CreateRecipeActivityListener)
            activityApp = context
        else
            Log.d("LOG", "${this::class} error attaching activity listener")
    }

    fun removeIngredient(name: String)
    {
        val list = mutableListOf<Pair<FoodAccess, Float>>()
        for (ing in ingList)
        {
            if (!ing.first.whatName().equals(name))
                list.add(Pair(ing.first, ing.second))
        }
        ingList = list
        rvEntries.layoutManager = LinearLayoutManager(context)
        rvEntries.adapter = CreateRecipeAdapter(ingList, this)
    }

    private fun createRecipe(): Recipe
    {
        val recipeName = createRecipeNameVal.text.toString()
        val portionQty = createRecipePortionQty.text.toString().toInt()
        val recipe = Recipe(recipeName, portionQty)
        for (pair in ingList)
        {
            recipe.addIngredient(pair.first as Ingredient, pair.second)
        }
        return recipe
    }

    private fun recipeExists(): Boolean
    {
        val activity = requireActivity() as MainActivity
        val recipe = createRecipe()
        Log.d("LOG", "${this::class.java} attempting to insert recipe: $recipe")
        return !activity.app.dbHelper.insertRecipe(recipe)
    }

    private fun checkInputValid(): Boolean
    {
        hideErrors()
        Log.d("LOG", "${this::class.java} checking valid input")
        var valid = true
        if (ingList.size == 0)
        {
            createRecipeIngErr.visibility = TextView.VISIBLE
            valid = false
        }
        if (createRecipeNameVal.text.toString().equals(""))
        {
            createRecipeErrEmpty.visibility = TextView.VISIBLE
            valid = false
        }
        if (createRecipePortionQty.text.toString().equals(""))
        {
            createRecipePortionErr.visibility = TextView.VISIBLE
            valid = false
        }
        return valid
    }

    private fun hideErrors()
    {
        Log.d("LOG", "${this::class.java} hiding error messages")
        createRecipeErrDuplicate.visibility = TextView.GONE
        createRecipeErrEmpty.visibility = TextView.GONE
        createRecipeIngErr.visibility = TextView.GONE
        createRecipePortionErr.visibility = TextView.GONE
    }

    override fun updateSet(ingFrag: ViewInredientsFragment) {
        Log.d("LOG", "${this::class.java} updating adapter set")
        var set: SortedSet<String> = sortedSetOf()
        val activity = requireActivity() as MainActivity
        set = activity.app.dbHelper.getIngredients()
        for (pair in ingList)
        {
            var name = pair.first.whatName()
            if (pair.first.whatType() == FoodType.INGREDIENT)
                    set.remove(name)
        }
        ingFrag.set = set
        ingFrag.rvEntries.adapter = ViewIngredientsInsertAdapter(set, ingFrag)
    }

    // ingredient has been added from ViewIngredientsFragment, update list
    override fun onInsertFullIng(name: String, qty: Float) {
        var activity = requireActivity() as MainActivity
        var ing: FoodAccess =  activity.app.dbHelper.getIngredient(name)!!
        ingList.add(Pair<FoodAccess, Float>(ing, qty))
        rvEntries.adapter?.notifyDataSetChanged()
        activity.detachFragment(ViewInredientsFragment.FRAGMENT_TAG)
        setMoreDetailsButtonVisible()
        hideErrors()
        if (finishClicked)
            checkInputValid()
    }
}
