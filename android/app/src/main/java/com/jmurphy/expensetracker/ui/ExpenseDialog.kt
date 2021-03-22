package com.jmurphy.expensetracker.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.jmurphy.expensetracker.ExpenseViewModel
import com.jmurphy.expensetracker.R
import com.jmurphy.expensetracker.data.Category
import com.jmurphy.expensetracker.data.Expense
import me.abhinay.input.CurrencyEditText
import org.w3c.dom.Text
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ExpenseDialog: DialogFragment() {

    private val viewModel by activityViewModels<ExpenseViewModel>()

    private lateinit var nameInput: EditText
    private lateinit var categoryInput: AutoCompleteTextView
    private lateinit var dateSelectBtn: RelativeLayout
    private lateinit var currencySymbol: TextView
    private lateinit var amountInput: CurrencyEditText
    private lateinit var submitBtn: MaterialButton

    private lateinit var dateText: TextView

    private var selectedCategory: Category? = null

    var selectedDate = LocalDate.now()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.expense_report, null)

        setupView(view)
        setupClickListener(view)

        viewModel.getCategories().observe(this, { categories ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
            categoryInput.setAdapter(adapter)
            categoryInput.setOnItemClickListener { parent, view, position, id ->
                val category = categories.get(position)
                selectedCategory = category
            }
            categoryInput.doOnTextChanged { text, start, before, count ->
                selectedCategory = null
            }
        })

        return AlertDialog.Builder(requireContext())
                .setView(view)
                .create()
    }

    private fun setupView(view: View) {
        nameInput = view.findViewById(R.id.name_input)
        categoryInput = view.findViewById(R.id.category_input)
        dateSelectBtn = view.findViewById(R.id.date_select_btn)
        dateText = view.findViewById(R.id.date)
        amountInput = view.findViewById(R.id.amount_input)
        submitBtn = view.findViewById(R.id.submit_btn)
        currencySymbol = view.findViewById(R.id.currency_symbol)

        dateText.text = selectedDate.format(DateTimeFormatter.ISO_DATE)
        currencySymbol.text = viewModel.getCurrency()
    }

    private fun setupClickListener(view: View) {
        val dialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month+1, dayOfMonth)
            dateText.text = getFormattedDate()

        }, selectedDate.year, selectedDate.monthValue, selectedDate.dayOfMonth)
        dialog.datePicker.maxDate = System.currentTimeMillis()

        dateSelectBtn.setOnClickListener{
            dialog.show()
        }
        submitBtn.setOnClickListener{


            if (!requiredFieldsMissing()){

                viewModel.getCategories().removeObservers(this)

                val currency = NumberFormat.getCurrencyInstance()
                val name = nameInput.text.toString()
                val amount = amountInput.cleanDoubleValue
                val date = dateText.text.toString()

                if (selectedCategory==null){
                    viewModel.createExpense(
                        categoryInput.text.toString(),
                        Expense(
                            item_name = name,
                            amount = amount,
                            date = getFormattedDate()
                        )
                    )
                }
                else{
                    viewModel.createExpense(
                        Expense(
                            selectedCategory,
                            name,
                            amount,
                            getFormattedDate()
                        )
                    )
                }

                dismiss()
            }
        }
    }

    private fun getFormattedDate(): String {
        return selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private fun requiredFieldsMissing(): Boolean {

        if (nameInput.text.toString().equals("")){
            Snackbar.make(requireParentFragment().requireView(), "Enter name", Snackbar.LENGTH_SHORT).show()
            return true
        }
        if (amountInput.text.toString().equals("")){
            Snackbar.make(requireParentFragment().requireView(), "Enter amount", Snackbar.LENGTH_SHORT).show()
            return true
        }
        if (categoryInput.text.toString().equals("")){
            Snackbar.make(requireParentFragment().requireView(), "Enter category", Snackbar.LENGTH_SHORT).show()
            return true
        }

        return false
    }

    override fun onDetach() {
        viewModel.getCategories().removeObservers(requireActivity())
        super.onDetach()
    }

    companion object {
        val TAG = ExpenseDialog::class.simpleName

        fun newInstance() = ExpenseDialog()
    }
}