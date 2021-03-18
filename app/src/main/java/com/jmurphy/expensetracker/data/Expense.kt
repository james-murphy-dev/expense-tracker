package com.jmurphy.expensetracker.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Expense(
        @Embedded(prefix = "type_") var category: Category? = null,

        @ColumnInfo
        val item_name: String,

        @ColumnInfo
        val amount: Double,

        @ColumnInfo
        val date: String,

        @PrimaryKey(autoGenerate = true) var uid: Long = 0,
)

@Entity
data class Category(
        @ColumnInfo
        val name: String,

        @PrimaryKey(autoGenerate = true) var uid: Long = 0
){
        override fun toString(): String = name
}

enum class DateRange{
        DAY, WEEK, MONTH, YEAR, ALL
}

data class FilterOptions(
        var dateRange: DateRange?
)