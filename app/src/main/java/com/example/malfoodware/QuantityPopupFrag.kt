package com.example.malfoodware

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.quantity_popup_fragment.*

class QuantityPopupFrag: AppCompatDialogFragment()  {

    var contHeight: Int = 0

    companion object
    {
        val QTY_POPUP_FRAG_TAG = "qtyPopupTag"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.quantity_popup_fragment, container, false)
        return v
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val serving = requireActivity().intent.getFloatExtra("defQty", 0f)
        qtyPopupServinID.setText("x Serving (${serving.toInt()}g)")
        contHeight = qtyPopupContainer.layoutParams.height
        addIngredientFinalize.setOnClickListener {
            if (checkValidInputGram()) {
                hideInvalidInputGram()
                hideInvalidInputServing()
                finalizeSetGram()
            }
            else
            {
                hideInvalidInputServing()
                displayInvalidInputGram()
            }
        }
        qtyInputGram.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
        addIngredientFinalizeServingQty.setOnClickListener {
            if (checkValidInputServing()) {
                hideInvalidInputServing()
                hideInvalidInputGram()
                finalizeSetServing()
            }
            else {
                hideInvalidInputGram()
                displayInvalidInputServing()
            }
        }
        qtyInputServing.setOnFocusChangeListener { view, b ->
            if (!b) view.hideKeyboard()
        }
    }

    private fun checkValidInputGram(): Boolean
    {
        val text = qtyInputGram.text.toString()
        return !text.equals("")
    }

    private fun checkValidInputServing(): Boolean
    {
        val text = qtyInputServing.text.toString()
        return !text.equals("")
    }

    private fun displayInvalidInputGram()
    {
        qtyInvalidInputGram.visibility = (TextView.VISIBLE)
        qtyInvalidInputGram.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    private fun hideInvalidInputGram()
    {
        qtyInvalidInputGram.visibility = (TextView.GONE)
        qtyInvalidInputGram.layoutParams.height = 0
        qtyPopupContainer.requestLayout()
    }

    private fun displayInvalidInputServing()
    {
        qtyInvalidInputServing.visibility = TextView.VISIBLE
        qtyInvalidInputServing.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    private fun hideInvalidInputServing()
    {
        qtyInvalidInputServing.visibility = TextView.GONE
    }

    private fun finalizeSetGram()
    {
        val qty = view?.findViewById<EditText>(R.id.qtyInputGram)
        val value = qty?.text.toString().toFloat()
        targetFragment!!.onActivityResult(
            targetRequestCode,
            Activity.RESULT_OK,
            requireActivity().intent.putExtra("qty", value)
        )
        dismiss()
        onStop()
    }

    private fun finalizeSetServing()
    {
        val qty = qtyInputServing.text.toString().toInt()
        val value = qty * requireActivity().intent.getFloatExtra("defQty", 0f)
        targetFragment!!.onActivityResult(
            targetRequestCode,
            Activity.RESULT_OK,
            requireActivity().intent.putExtra("qty", value)
        )
        dismiss()
        onStop()
    }
}