package com.example.malfoodware

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.delete_popup.*

class DeletePopupFrag(var type: FoodType): AppCompatDialogFragment()  {

    companion object
    {
        val QTY_POPUP_FRAG_TAG = "deletePopup"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.delete_popup, container, false)
        return v
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deletePopupYesBut.setOnClickListener {
            finishYes()
        }
        deletePopupNoBut.setOnClickListener {
            finishNo()
        }
        val message = errorPopopMessage
        when (type)
        {
            FoodType.INGREDIENT -> message.setText(
                "Are you sure you want to remove Ingredient?\n\nAny recipes will need to be ammended," +
                        " and recipes will be highlighted in red in view recipes\n\nAny diary entries will no longer show this"
            )
            FoodType.RECIPE -> message.setText(
                "Are you sure you want to remove Recipe?\nAny diary entries will no longer show this"
            )
        }
    }

    private fun finishYes()
    {
        targetFragment!!.onActivityResult(
            targetRequestCode,
            Activity.RESULT_OK, requireActivity().intent
        )
        dismiss()
        onStop()
    }

    private fun finishNo()
    {
        targetFragment!!.onActivityResult(
            targetRequestCode,
            Activity.RESULT_CANCELED, requireActivity().intent
        )
        dismiss()
        onStop()
    }
}