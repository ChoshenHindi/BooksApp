package com.example.booksapp.database

import androidx.room.TypeConverter
import com.example.booksapp.entities.User
import com.google.gson.Gson

class UserConverter {
    @TypeConverter
    fun fromUser(user: User?): String? {
        return user?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toUser(data: String?): User? {
        return data?.let { Gson().fromJson(it, User::class.java) }
    }
}