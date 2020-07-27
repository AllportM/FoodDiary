package com.example.malfoodware

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CreateRecipeAdapter(val mEntries: MutableList<Pair<Ingredient, Float>>, val ingFrag: CreateRecipeFragment):
    RecyclerView.Adapter<CreateRecipeAdapter.ViewHolder>()
{
    private lateinit var view1: View

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        var ingredient_name = itemView.findViewById<TextView>(R.id.createEntryItemName)
        var qty = itemView.findViewById<TextView>(R.id.createEntryItemQty)
        var delete = itemView.findViewById<TextView>(R.id.createEntryItemRemove)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.diary_entry_create_row, parent, false)
        view1 = parent.rootView
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return mEntries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingName = mEntries.elementAt(position).first.name
        val qty = mEntries.elementAt(position).second
        holder.ingredient_name.setText(ingName)
        holder.qty.setText(Math.round(qty).toString())
        holder.delete.setOnClickListener {
            ingFrag.removeIngredient(ingName)
            notifyDataSetChanged()
        }
    }
}