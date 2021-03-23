package com.jmurphy.expensetracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expense ORDER BY date DESC")
    fun getAll(): LiveData<List<Expense>>

    @Query("SELECT * FROM expense WHERE uid IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Expense>

    @Insert
    suspend fun insert(expense: Expense): Long

    @Insert
    suspend fun insertAll(vararg expenses: Expense)

    @Delete
    suspend fun delete(user: Expense)
}