package com.example.malfoodware

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        FileHelper.context = applicationContext
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println(applicationContext.filesDir)
        println("App says hello")
        var app: App = App(applicationContext)
    }
}
