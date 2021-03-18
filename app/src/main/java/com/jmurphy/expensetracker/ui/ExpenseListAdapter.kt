package com.jmurphy.expensetracker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jmurphy.expensetracker.R
import com.jmurphy.expensetracker.data.DateRange
import com.jmurphy.expensetracker.data.Expense
import com.jmurphy.expensetracker.databinding.ExpenseItemViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class ExpenseListAdapter(val context: Context, val clickListener: ExpenseListener)
    : ListAdapter<Expense, ExpenseListAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private var currencySymbol: String

    init {
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE)
        currencySymbol = sharedPreferences.getString(context.getString(R.string.local_currency), "$")!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder.from(parent)
    }

    fun setData(data : List<Expense>){
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(data)
            }
        }
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, currencySymbol, clickListener)
    }

    class ExpenseViewHolder private constructor(val binding: ExpenseItemViewBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Expense, currencySymbol: String, clickListener: ExpenseListener){
            binding.expense = item
            binding.currency = currencySymbol
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ExpenseViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ExpenseItemViewBinding.inflate(layoutInflater, parent, false)
                return ExpenseViewHolder(binding)
            }
        }
    }
}

class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
    override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem == newItem
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem.uid.equals(newItem.uid)
    }
}


class ExpenseListener(val clickListener: (expense: Expense) -> Unit) {
    fun onClick(expense: Expense) = clickListener(expense)
}