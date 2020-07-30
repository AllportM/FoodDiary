package com.example.malfoodware

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*

class ViewIngredientEditAdapter(mentries: SortedSet<String>, val ingFrag: ViewInredientsFragment): ViewIngredientsAdapterParent(mentries) {
    private lateinit var activityApp: ViewIngredientsEditAdapterListener
    private lateinit var view1: View

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : ViewIngredientsAdapterParent.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        var ingredient_name = itemView.findViewById<TextView>(R.id.ingredient_view_FE_name)
        var delete = itemView.findViewById<Button>(R.id.ingredient_rmv_FE_but)
        var edit = itemView.findViewById<Button>(R.id.inredient_edit_FE_but)
    }

    interface ViewIngredientsEditAdapterListener
    {
        fun onDeleteFoodFromViewIng(name: String, ingFrag: ViewInredientsFragment)
        fun onAmmentFoodFromViewIng(name: String, ingFrag: ViewInredientsFragment)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        if (parent.context is ViewIngredientsEditAdapterListener)
        {
            activityApp = parent.context as ViewIngredientsEditAdapterListener
        }
        else
        {
            Log.d("LOG", "${this::class.java} Could not convert context to ViewIngrediensEditAdapter")
        }
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.view_inredients_row_ammend, parent, false)
        view1 = parent.rootView
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return mEntries.size
    }

    override fun onBindViewHolder(holder: ViewIngredientsAdapterParent.ViewHolder, position: Int) {
        val holder = holder as ViewHolder
        val ingName = mEntries.elementAt(position)
        holder.ingredient_name.setText(ingName)
        holder.edit.setOnClickListener {
            val text = view1.findViewById<EditText>(R.id.viewIngFilterText)
            if (!text.isFocused)
                activityApp.onAmmentFoodFromViewIng(ingName, ingFrag)
            else
            {
                text.clearFocus()
                view1.hideKeyboard()
            }
        }
        holder.delete.setOnClickListener {
            val text = view1.findViewById<EditText>(R.id.viewIngFilterText)
            if (!text.isFocused)
                activityApp.onDeleteFoodFromViewIng(ingName, ingFrag)
            else
            {
                text.clearFocus()
                view1.hideKeyboard()
            }
        }
        // when is recipe, and has deleted ingredient, sets background to red
        when (ingFrag.type)
        {
            FoodType.RECIPE ->
            {
                val activity = ingFrag.requireActivity() as MainActivity
                val recipe = activity.app.dbHelper.getRecipe(mEntries.elementAt(position))!!
                if (recipe.hasDeleteIng)
                {
                    (holder.ingredient_name.parent.parent as View).setBackgroundResource(R.color.deleted)
                }
            }
        }
    }
}