package com.example.malfoodware

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.menu_fragment.*


class MenuFragment: Fragment()  {

    var contHeight: Int = 0

    companion object
    {
        val FRAG_TAG = "menuPopup"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.menu_fragment, container, false)
        return v
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuBackDrop.alpha = 0.5f
        menuButCont.alpha = 1f
        val activity = requireActivity() as MainActivity
        menuBackDrop.setOnClickListener {
            activity.detachFragment(FRAG_TAG)
        }
    }
}