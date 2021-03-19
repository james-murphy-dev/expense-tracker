package com.jmurphy.expensetracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseTypeDao {

    @Query("SELECT * FROM category")
    fun getAll(): LiveData<List<Category>>

    @Query("SELECT * FROM category WHERE uid IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Category>

    @Insert
    fun insert(category: Category): Long

    @Insert
    fun insertAll(vararg categories: Category)

    @Delete
    fun delete(user: Category)

    @Query("SELECT * FROM category WHERE uid=:id LIMIT 1")
    fun findById(id: Long): Category
}