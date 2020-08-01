package com.example.malfoodware

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.menu_fragment.*


class MenuFragment: Fragment()  {

    companion object
    {
        val FRAG_TAG = "menuPopup"
    }

    interface MenuActivityListener
    {
        fun onOpenSettings()
        fun onExport()
        fun onLogout()
    }

    lateinit var activityApp: MenuActivityListener

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
        menuSettingsBut.setOnClickListener {
            activityApp.onOpenSettings()
        }
        menuExportBut.setOnClickListener {
            activityApp.onExport()
        }
        menuLogoutBut.setOnClickListener {
            activityApp.onLogout()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MenuActivityListener)
            activityApp = context
        else
            Log.d("LOG", "${this::class} error attaching activity listener")
    }
}