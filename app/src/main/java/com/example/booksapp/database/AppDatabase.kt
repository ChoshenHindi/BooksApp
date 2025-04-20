package com.example.booksapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.booksapp.entities.Comment
import com.example.booksapp.entities.Post
import com.example.booksapp.entities.User

@Database(entities = [User::class, Post::class, Comment::class], version = 6)
@TypeConverters(UserConverter::class, StringsConverter::class, CommentConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}