package com.example.malfoodware

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CreateEntryViewAdapter(private val mEntries: List<Pair<FoodAccess, Float>>, private val frag: CreateEntryViewFragment) :
    RecyclerView.Adapter<CreateEntryViewAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        var itemName = itemView.findViewById<TextView>(R.id.createEntryViewItemName)
        var qty = itemView.findViewById<TextView>(R.id.createEntryViewItemQty)
    }

    lateinit var activityApp: CreateEntryViewListener
    private var elementList: MutableMap<Pair<FoodAccess, Float>, View> = mutableMapOf()
    private var clicked: View? = null

    interface CreateEntryViewListener
    {
        fun showFoodEntryFocussed(entry: Pair<FoodAccess, Float>)
        fun hideFoodEntryFocussed(frag: CreateEntryViewFragment)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        if (parent.context is CreateEntryViewListener)
        {
            activityApp = parent.context as CreateEntryViewListener
        }
        else
        {
            Log.d("LOG", "Could not convert context to FoodEntryListener")
        }
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.diary_entry_create_view_row, parent, false)
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
        val itemName = holder.itemName
        val parentView = itemName.parent.parent as View
        parentView.setOnClickListener(EntryClickListener(food))
        elementList[food] = parentView
    }

    inner class EntryClickListener(val entry: Pair<FoodAccess, Float>): View.OnClickListener
    {
        @SuppressLint("ResourceAsColor")
        override fun onClick(p0: View?) {
            clearSelection()
            val view = elementList[entry]
            if(clicked != null && clicked!!.equals(view))
            {
                Log.d("LOG", "Food entry clicked twice, detaching")
                view!!.setBackgroundColor(Color.parseColor("#cff1ed"))
                activityApp.hideFoodEntryFocussed(frag)
                clicked = null
            }
            else
            {
                activityApp.hideFoodEntryFocussed(frag)
                Log.d("LOG", "Food entry clicked for first time, attaching")
                clicked = view
                view!!.setBackgroundColor(MainActivity.COL_LIST_DARK)
                activityApp.showFoodEntryFocussed(entry)
            }
        }

    }

    fun clearSelection()
    {
        for(element in elementList)
        {
            element.value.setBackgroundColor(MainActivity.COL_LIST_LIGHT)
        }
    }

}