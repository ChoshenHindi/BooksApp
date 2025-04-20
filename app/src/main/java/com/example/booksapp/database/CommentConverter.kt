package com.example.booksapp.database

import androidx.room.TypeConverter
import com.example.booksapp.entities.Comment
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class CommentConverter {
    @TypeConverter
    fun fromList(comments: ArrayList<Comment>?): String? {
        val gson = Gson()
        return gson.toJson(comments)
    }

    @TypeConverter
    fun toList(data: String?): ArrayList<Comment>? {
        val listType = object : TypeToken<ArrayList<Comment>>() {}.type
        data?.let {
            return Gson().fromJson(data, listType)
        } ?: run {
            return arrayListOf()
        }
    }
}