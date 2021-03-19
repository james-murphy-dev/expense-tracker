package com.jmurphy.expensetracker

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.jmurphy.expensetracker.data.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture

class ExpenseViewModel(val context: Application): AndroidViewModel(context) {

    private val expensesRepo = ExpensesRepository(context)

    private lateinit var allExpenses: List<Expense>
    private val allExpensesLiveData = expensesRepo.getExpenses()

    private val filteredExpensesLiveData = MediatorLiveData<List<Expense>>()
    private val filterLiveData = MutableLiveData<FilterOptions?>()
    private val selectedExpense = MutableLiveData<Expense>()

    private val filterDay = context.getString(R.string.date_range_day)
    private val filterWeek = context.getString(R.string.date_range_week)
    private val filterMonth = context.getString(R.string.date_range_month)
    private val filterYear = context.getString(R.string.date_range_year)

    init {

        createSharedPrefs(context)

        filteredExpensesLiveData.addSource(allExpensesLiveData){ expenses ->
            allExpenses = expenses
            filteredExpensesLiveData.value = allExpenses
        }

        filteredExpensesLiveData.addSource(filterLiveData){ filter ->
            var currentDate = LocalDate.now()

            val startDate = when(filter?.dateRange){
                DateRange.DAY -> currentDate.minusDays(1)
                DateRange.WEEK -> currentDate.minusWeeks(1)
                DateRange.MONTH -> currentDate.minusMonths(1)
                DateRange.YEAR -> currentDate.minusYears(1)
                DateRange.ALL -> null
                else -> null
            }

            if (startDate==null){
                filteredExpensesLiveData.value = allExpenses
            }
            else{

                val filtered = allExpenses.filter {
                    val date = LocalDate.parse(it.date)
                    date.isAfter(startDate) || date.isEqual(startDate)
                }

                filteredExpensesLiveData.value = filtered
            }
        }

    }

    private fun createSharedPrefs(context: Context) {
        val currencyKey = context.getString(R.string.local_currency)

        val currencySymbol = Currency.getInstance(Locale.getDefault()).symbol
        val sharedPrefs = context?.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE)

        if (!sharedPrefs.contains(currencyKey)){
            sharedPrefs?.edit()?.putString(currencyKey, currencySymbol)?.apply()
        }
    }

    fun getExpenseList(): LiveData<List<Expense>>{
        return filteredExpensesLiveData
    }

    fun getCategories(): LiveData<List<Category>> {
        return expensesRepo.getExpenseTypes()
    }

    fun setSelectedExpense(expense: Expense) {
        selectedExpense.value = expense
    }

    fun getFilters(): LiveData<FilterOptions?> {
        return filterLiveData
    }

    fun getSelectedExpense(): LiveData<Expense> {
        return selectedExpense
    }

    fun setFilter(id: String) {
        var dateRange: DateRange = when (id){
            filterDay -> DateRange.DAY
            filterWeek -> DateRange.WEEK
            filterMonth -> DateRange.MONTH
            filterYear -> DateRange.YEAR
            else -> DateRange.ALL
        }

        var filter = filterLiveData.value
        if (filter==null){
            filter = FilterOptions(dateRange)
        }
        else{
            filter.dateRange = dateRange
        }
        filterLiveData.value = filter
    }

    fun createExpense(expense: Expense) {
        CompletableFuture.runAsync{
            expensesRepo.createExpense(expense)
        }
    }

    fun createExpense(category: String, expense: Expense) {
        CompletableFuture.runAsync{
            expense.category = createCategory(category)
            expensesRepo.createExpense(expense)
        }
    }

    private fun createCategory(name: String): Category {
        val id = expensesRepo.createCategory(
            Category(
                name
            )
        )

        return expensesRepo.getCategory(id)
    }

    fun getCurrency(): String {
        val prefs = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE)
        return prefs.getString(context.getString(R.string.local_currency), "$")!!
    }


}
