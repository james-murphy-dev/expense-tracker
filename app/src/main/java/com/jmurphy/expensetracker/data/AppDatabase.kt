package com.jmurphy.expensetracker.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = arrayOf(Expense::class, Category::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): ExpenseTypeDao

}