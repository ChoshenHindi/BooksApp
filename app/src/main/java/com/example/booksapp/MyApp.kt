package com.example.booksapp

import android.app.Application
import com.example.booksapp.database.RoomManager

class MyApp : Application() {


    override fun onCreate() {
        super.onCreate()
        RoomManager.initManager(this)
    }
}