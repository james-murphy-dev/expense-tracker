package com.jmurphy.expensetracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jmurphy.expensetracker.ExpenseViewModel
import com.jmurphy.expensetracker.R
import com.jmurphy.expensetracker.data.Expense
import com.jmurphy.expensetracker.databinding.ItemListBinding
import java.text.NumberFormat
import java.util.*

class ExpenseListFragment: Fragment() {

    private val viewModel by activityViewModels<ExpenseViewModel>()

    private lateinit var adapter: ExpenseListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var addExpenseBtn: FloatingActionButton

    private var _binding: ItemListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {


        _binding = ItemListBinding.inflate(inflater, container, false)

        val view = binding.root

        addExpenseBtn = view.findViewById(R.id.create_expense_btn)
        progressBar = view.findViewById(R.id.progress)
        recyclerView = view.findViewById(R.id.list)

        adapter = ExpenseListAdapter( requireContext(), ExpenseListener {

            viewModel.setSelectedExpense(it)

        })
        recyclerView.adapter = adapter

        viewModel.getExpenseList().observe(viewLifecycleOwner, { expenses ->
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.setData(expenses)

            var total = expenses.map { it.amount }.sum()
            var totalStr = "%.2f".format(total)
            binding.total.text = totalStr
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        })

        binding.currencySymbol.text = viewModel.getCurrency()

        viewModel.getFilters().observe(viewLifecycleOwner, { filters ->
            binding.dateRange.text = filters?.dateRange.toString()
        })

        addExpenseBtn.setOnClickListener{
            ExpenseDialog().show(childFragmentManager, ExpenseDialog.TAG)

        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ExpenseListFragment()
    }

}