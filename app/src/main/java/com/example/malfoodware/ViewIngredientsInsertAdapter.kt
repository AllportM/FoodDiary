package com.example.malfoodware

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ViewIngredientsInsertAdapter(mEntries: SortedSet<String>, val ingFrag: ViewInredientsFragment):
        ViewIngredientsAdapterParent(mEntries)
{
    private lateinit var activityApp: ViewIngredientsAdapterListener
    private lateinit var view1: View

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : ViewIngredientsAdapterParent.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        var ingredient_name = itemView.findViewById<TextView>(R.id.ingredient_view_name)
        var add = itemView.findViewById<TextView>(R.id.add_ingredient_from_view)
    }

    interface ViewIngredientsAdapterListener
    {
        fun onInsertIngredient(name: String, ingFrag: ViewInredientsFragment)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        if (parent.context is ViewIngredientsAdapterListener)
        {
            activityApp = parent.context as ViewIngredientsAdapterListener
        }
        else
        {
            Log.d("LOG", "Could not convert context to FoodEntryListener")
        }
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.view_ingredient_row_add, parent, false)
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
        holder.add.setOnClickListener {
            val text = view1.findViewById<EditText>(R.id.viewIngFilterText)
            if (!text.isFocused)
                activityApp.onInsertIngredient(ingName, ingFrag)
            else
            {
                text.clearFocus()
                view1.hideKeyboard()
            }
        }
    }
}