package com.jmurphy.expensetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.jmurphy.expensetracker.ExpenseViewModel
import com.jmurphy.expensetracker.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FilterDrawerFragment: Fragment() {

    private val viewModel by activityViewModels<ExpenseViewModel>()

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.filter_drawer_layout, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE
        val currentDate = LocalDateTime.now()
        val currentDateIso = currentDate.format(dateFormat)
        val yesterday = currentDate.minusDays(1).format(dateFormat)
        val lastWeek = currentDate.minusWeeks(1).format(dateFormat)
        val lastMonth = currentDate.minusMonths(1).format(dateFormat)
        val lastYear = currentDate.minusYears(1).format(dateFormat)
        val filters = listOf(
            Filter(
                id = getString(R.string.date_range_all),
                name = "All",
                description = "All transactions"
            ),
            Filter(
                id = getString(R.string.date_range_day),
                name = "Day",
                description = "${yesterday} - ${currentDateIso}"
            ),
            Filter(
                id = getString(R.string.date_range_week),
                name = "Week",
                description = "${lastWeek} - ${currentDateIso}"
            ),
            Filter(
                id = getString(R.string.date_range_month),
                name = "Month",
                description = "${lastMonth} - ${currentDateIso}"
            ),
            Filter(
                id = getString(R.string.date_range_year),
                name = "Year",
                description = "${lastYear} - ${currentDateIso}"
            )
        )

        val adapter = FilterListAdapter(FilterListener {
            viewModel.setFilter(it)
        })

        adapter.addHeaderAndSubmitList("Date range", getString(R.string.filter_date_range), filters)

        recyclerView.adapter = adapter

    }
    companion object {
        fun newInstance() = FilterDrawerFragment()
    }
}