package com.example.ContextAwareRinger.Activities

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ContextAwareRinger.Data.ActivityData
import com.example.ContextAwareRinger.readFromFile
import com.example.ContextAwareRinger.writeToFile
import com.example.myapplication.R

//TODO: Change UI to be loading screen that checks permissions
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
Added Util class with methods to read/write an object to file. Made data objects Serializable.