package com.example.malfoodware

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_ingredients.*
import java.util.*

class ViewInredientsFragment: Fragment() {

    lateinit var set: SortedSet<String>
    lateinit var type: FoodType

    companion object
    {
        val FRAGMENT_TAG = "viewIngedientsFrag"
    }

    interface ViewIngredientsFragmentListener
    {
        fun onInsertFullIng(name: String, qty: Float)
        fun updateSet(ingFrag: ViewInredientsFragment)
        fun onCreateIngredient()
        fun onCreateRecipe()
    }

    lateinit var view1: View
    lateinit var rvEntries: RecyclerView
    lateinit var activityApp: ViewIngredientsFragmentListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.view_ingredients, container, false)
        return v
    }

    // init variables
    // initilaizes ViewTexts and sets the text values for given entry
    override fun onStart() {
        super.onStart()
    }

    // init listener iinterface to app
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewIngredientsFragmentListener)
            activityApp = context
        else
            Log.d("INITLOG", "Error attaching listener in ${this::class}, context must " +
                    "implement ViewIngredientsFragment interface")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view1 = view
        rvEntries = view1.findViewById(R.id.viewIngredientsRecycler) as RecyclerView
        rvEntries.layoutManager = LinearLayoutManager(context)
        viewIngFilterText.doOnTextChanged { textInput, start, count, after ->
            updateAdapter(textInput.toString())
        }
        viewIngFilterText.setOnFocusChangeListener { view, b ->
            if (!b)
                view.hideKeyboard()
        }
        createIngBackBut.setOnClickListener {
            activity?.onBackPressed()
        }
        viewIngredientCreateBut.setOnClickListener {
            when (type) {
                FoodType.INGREDIENT -> activityApp.onCreateIngredient()
                FoodType.RECIPE -> activityApp.onCreateRecipe()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activityApp.updateSet(this)
    }

    // override fragment response
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check for the results
        if (requestCode == CreateEntryActivity.INREDIENT_REQ_CODE && resultCode == Activity.RESULT_OK) {
            // get values from data
            val qty = data!!.getFloatExtra("qty", 0f)
            val name = data.getStringExtra("name")!!
            activityApp.onInsertFullIng(name, qty)
        }
    }

    // filters adapters list based on user input text
    private fun updateAdapter(input: String)
    {
        val adapter = rvEntries.adapter as ViewIngredientsAdapterParent
        if (input.equals(""))
        {
            adapter.mEntries = set
            adapter.notifyDataSetChanged()
        }
        else
        {
            val newSet = sortedSetOf<String>()
            for (ing in set)
            {
                if (ing.toLowerCase().contains(input.toLowerCase()))
                {
                    newSet.add(ing)
                }
            }
            adapter.mEntries = newSet
            adapter.notifyDataSetChanged()
        }
    }
}