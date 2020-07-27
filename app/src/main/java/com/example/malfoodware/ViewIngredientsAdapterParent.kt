package com.example.malfoodware

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class ViewIngredientsAdapterParent(var mEntries: SortedSet<String>):
    RecyclerView.Adapter<ViewIngredientsAdapter.ViewHolder>() {
}