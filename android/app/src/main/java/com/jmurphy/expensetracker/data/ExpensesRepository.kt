package com.jmurphy.expensetracker.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room

class ExpensesRepository(context: Context) {

    val db = Room.databaseBuilder(
        context, AppDatabase::class.java, "expense-tracker")
        .createFromAsset("databases/expenses.db")
        .build()

    fun getExpenses(): LiveData<List<Expense>>{
        return db.expenseDao().getAll()
    }

    fun getExpenseTypes(): LiveData<List<Category>>{
        return db.categoryDao().getAll()
    }

    suspend fun createExpense(expense: Expense): Long {
        return db.expenseDao().insert(expense)
    }

    suspend fun createCategory(category: Category): Long{
        return db.categoryDao().insert(category)
    }

    suspend fun getCategory(id: Long): Category {
        return db.categoryDao().findById(id);
    }
}
