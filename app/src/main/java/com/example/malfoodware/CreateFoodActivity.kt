package com.example.malfoodware

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.create_food_activity.*
import java.util.*

class CreateFoodActivity:
    AppCompatActivity(),
        CreateIngFragment.CreateInredientActivityListener,
        CreateRecipeFragment.CreateRecipeActivityListener,
        ViewInredientsFragment.ViewIngredientsFragmentListener,
        ViewIngredientsAdapter.ViewIngredientsAdapterListener
{
    lateinit var app: App
    lateinit var type: FoodType
    lateinit var recipeFrag: CreateRecipeFragment
    val FRAGMENT_EP = R.id.createFoodFragmentEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LOG", "Main activity created")
        app = App(applicationContext)
        app.login(intent.getStringExtra("UNAME")!!)
        setContentView(R.layout.create_food_activity)
        val type = intent.getStringExtra("type")
        when (type)
        {
            FoodType.INGREDIENT.toString() ->
            {
                this.type = FoodType.INGREDIENT
                attachCreateIng()
            }
            FoodType.RECIPE.toString() ->
            {
                this.type = FoodType.RECIPE
                attachCreateRec()
                setToolbarTitle(FoodType.RECIPE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        val uname = intent.getStringExtra("UNAME")
        if (uname != null) {
            app.login(uname)
            if (app.user != null)
                Log.d("LOG", "${this::class.java} Login via create entry activity successfull")
        }
    }

    override fun onBackPressed() {
        val backStackCount = supportFragmentManager.backStackEntryCount
        if (backStackCount > 0) {
            var fragTopStackTag =
                supportFragmentManager.getBackStackEntryAt(backStackCount - 1)
                    .name
            if (fragTopStackTag.equals(CreateIngFragment.FRAGMENT_ID) && type == FoodType.INGREDIENT) {
                finish()
            }
            else
            {
                super.onBackPressed()
                setToolbarTitle(FoodType.RECIPE)
            }
        }
        else
            super.onBackPressed()
    }

    private fun attachCreateIng()
    {
        setToolbarTitle(FoodType.INGREDIENT)
        attachFragment(CreateIngFragment(), CreateIngFragment.FRAGMENT_ID)
    }

    private fun attachCreateRec()
    {
        recipeFrag = CreateRecipeFragment()
        setToolbarTitle(FoodType.RECIPE)
        attachFragment(recipeFrag, CreateRecipeFragment.FRAGMENT_ID)
    }


    private fun setToolbarTitle(type: FoodType)
    {
        when (type)
        {
            FoodType.INGREDIENT -> supportActionBar?.setTitle("Create Ingredient")
            FoodType.RECIPE -> supportActionBar?.setTitle("Create Recipe")
        }
    }

    private fun attachFragment(fragment: Fragment, tag: String)
    {
        Log.d("LOG", "${this::class.java} attaching fragment $tag")
        var transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
        transaction.replace(FRAGMENT_EP, fragment, tag)
            .addToBackStack(tag)
        transaction.show(fragment)
        transaction.commit()
    }

    private fun detachFragment(tag: String)
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

    /**
     * Create ingredient fragment stuffs
     */
    override fun onCreateIngredient(ing: Ingredient): Boolean {
        return app.dbHelper.insertIngredient(ing)
    }

    override fun onIngredientCreated() {
        when(type)
        {
            FoodType.INGREDIENT -> finish()
            FoodType.RECIPE ->
            {
                onBackPressed()
            }
        }
    }

    /**
     * Create recipe fragment stuffs
     */
    override fun onRecipeAddIngredient() {
        addViewIngredientsFrag()
    }

    override fun onRecipeFinalize() {
        finish()
    }

    private fun addViewIngredientsFrag()
    {
        val ingredientsFrag = ViewInredientsFragment()
        ingredientsFrag.type = FoodType.INGREDIENT
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(FRAGMENT_EP, ingredientsFrag, ViewInredientsFragment.FRAGMENT_TAG)
        transaction.addToBackStack(ViewInredientsFragment.FRAGMENT_TAG)
        transaction.commit()
    }

    /**
     * viewIngredient fragment stuffs
     */

    override fun onInsertFullIng(name: String, qty: Float) {
        var ing: Ingredient =  app.dbHelper.getIngredient(name)!!
        recipeFrag.ingList.add(Pair<Ingredient, Float>(ing, qty))
        recipeFrag.rvEntries.adapter?.notifyDataSetChanged()
        detachFragment(ViewInredientsFragment.FRAGMENT_TAG)
    }

    override fun updateSet(ingFrag: ViewInredientsFragment) {
        var set: SortedSet<String> = sortedSetOf()
        set = app.dbHelper.getIngredients()
        for (pair in recipeFrag.ingList)
        {
            var name = pair.first.whatName()
            set.remove(name)
        }
        ingFrag.set = set
        ingFrag.rvEntries.adapter = ViewIngredientsAdapter(set, ingFrag)
    }

    override fun onCreateIngredient() {
        attachCreateIng()
    }

    override fun onCreateRecipe() {
    }

    override fun onInsertIngredient(name: String, ingFrag: ViewInredientsFragment) {
        val fm = supportFragmentManager
        val fragment = QuantityPopupFrag()
        fragment.setTargetFragment(ingFrag, CreateEntryActivity.INREDIENT_REQ_CODE)
        intent.putExtra("name", name)
        val ingRec: FoodAccess
        ingRec = app.dbHelper.getIngredient(name)!!
        intent.putExtra("defQty", ingRec.whatServing())
        fragment.show(fm, QuantityPopupFrag.QTY_POPUP_FRAG_TAG)
    }
}