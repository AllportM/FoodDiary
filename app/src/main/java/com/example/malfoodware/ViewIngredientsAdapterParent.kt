package com.example.malfoodware

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class ViewIngredientsAdapterParent(var mEntries: SortedSet<String>):
    RecyclerView.Adapter<ViewIngredientsAdapterParent.ViewHolder>() {

    abstract inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView)
}