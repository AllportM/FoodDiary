package com.example.malfoodware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class LoginFragment: Fragment() {

    companion object {
        val FRAGMENT_TAG = "loginFragment"
    }

    private lateinit var onLoginListener: LoginFragmentListener

    interface LoginFragmentListener{
        fun onLogin(uid: String)
        fun onCreateUser(uid: String)
    }

    // attaches listener to send messages fragment-to-activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginFragmentListener)
            onLoginListener = context
        else
            Log.d("INITLOG", "Error attaching listener in LoginFragment, context must implement onLogin interface")
    }

    // inflates this fragment page
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.login_fragment, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var uidText = view.findViewById<TextView>(R.id.unameInput)
        // sets to hide keyboard when focus lost
        uidText.setOnFocusChangeListener {
                view, b -> if(!b) view.hideKeyboard()
        }

        // sets button listeners
        var loginBut = view.findViewById<TextView>(R.id.loginBut)
        var createUserBut = view.findViewById<TextView>(R.id.createUser)
        loginBut.setTransformationMethod(null)
        createUserBut.setTransformationMethod(null)
        loginBut.setOnClickListener {
            var name: String = uidText.text.toString()
            onLogin(name)
        }
        createUserBut.setOnClickListener {
            var name: String = uidText.text.toString()
            onCreateUser(name)
        }
    }

    // sends login signal back to main activity
    fun onLogin(uid: String)
    {
        onLoginListener.onLogin(uid)
    }

    // sends create user signal back to main activity
    fun onCreateUser(uid: String)
    {
        onLoginListener.onCreateUser(uid)
    }

    override fun onResume() {
        super.onResume()
    }


}