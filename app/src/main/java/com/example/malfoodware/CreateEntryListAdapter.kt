package com.example.malfoodware

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CreateEntryListAdapter(private val mEntries: List<Pair<FoodAccess, Float>>) :
    RecyclerView.Adapter<CreateEntryListAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        var itemName = itemView.findViewById<TextView>(R.id.createEntryItemName)
        var qty = itemView.findViewById<TextView>(R.id.createEntryItemQty)
        var delete = itemView.findViewById<TextView>(R.id.createEntryItemRemove)
    }

    lateinit var activityApp: CreateEntryListAdapterListener

    interface CreateEntryListAdapterListener
    {
        fun onItemRemove(name: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        if (parent.context is CreateEntryListAdapterListener)
        {
            activityApp = parent.context as CreateEntryListAdapterListener
        }
        else
        {
            Log.d("LOG", "Could not convert context to FoodEntryListener")
        }
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.diary_entry_create_row, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return mEntries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = mEntries.get(position)
        val foodName = food.first.whatName()
        val foodQty = food.second
        holder.itemName.setText(foodName)
        holder.qty.setText("${foodQty.toInt()} g ")
        holder.delete.setOnClickListener {
            activityApp.onItemRemove(foodName)
        }
    }

}