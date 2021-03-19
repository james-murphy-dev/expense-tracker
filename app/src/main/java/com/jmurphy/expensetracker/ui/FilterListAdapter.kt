package com.jmurphy.expensetracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

import android.annotation.SuppressLint
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jmurphy.expensetracker.R
import com.jmurphy.expensetracker.databinding.FilterItemViewBinding
import com.jmurphy.expensetracker.databinding.HeaderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.HashMap

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class FilterListAdapter(val clickListener: FilterListener): ListAdapter<FilterListItem, RecyclerView.ViewHolder>(FilterDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private var selectedFilters = HashMap<String, Int>()

    fun addHeaderAndSubmitList(headerLabel: String, filterId: String, list: List<Filter>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(FilterListItem.Header(filterId, headerLabel))
                else -> listOf(FilterListItem.Header(filterId, headerLabel)) + list.mapIndexed { pos, value -> FilterListItem.Filter(value.id, filterId, value.name, value.description,  false) }
            }
            withContext(Dispatchers.Main) {

                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextViewHolder ->{
                val headerItem = getItem(position) as FilterListItem.Header
                holder.bind(headerItem)
            }
            is ViewHolder -> {
                val filterItem = getItem(position) as FilterListItem.Filter

                holder.bind(filterItem, clickListener)

                holder.itemView.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {

                    val selectedFilter = selectedFilters.get(filterItem.groupId)
                    if (selectedFilter != null) {
                        val previousItem = getItem(selectedFilter) as FilterListItem.Filter
                        val previousItemPos = selectedFilter
                        previousItem.selected = false
                        notifyItemChanged(previousItemPos)
                    }

                    selectedFilters.put(filterItem.groupId, position)

                    filterItem.selected = true
                    notifyItemChanged(position)

                    clickListener.onClick(filterItem.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FilterListItem.Header -> ITEM_VIEW_TYPE_HEADER
            is FilterListItem.Filter -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class TextViewHolder private constructor(val binding: HeaderBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = HeaderBinding.inflate(layoutInflater, parent, false)
                return TextViewHolder(view)
            }
        }

        fun bind(item: FilterListItem.Header) {
            binding.header = item.name
            binding.executePendingBindings()
        }

    }


    class ViewHolder private constructor(val binding: FilterItemViewBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: FilterListItem.Filter, clickListener: FilterListener) {

            binding.id = item.id
            binding.name = item.name
            binding.description = item.description
            binding.selected = item.selected
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FilterItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class FilterDiffCallback : DiffUtil.ItemCallback<FilterListItem>() {
    override fun areItemsTheSame(oldItem: FilterListItem, newItem: FilterListItem): Boolean {
        return oldItem == newItem
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: FilterListItem, newItem: FilterListItem): Boolean {
        return oldItem.name.equals(newItem.name)
    }
}


class FilterListener(val clickListener: (filter: String) -> Unit) {
    fun onClick(filter: String) = clickListener(filter)
}

sealed class FilterListItem {
    data class Filter(override val id: String, val groupId: String, override val name: String,  val description: String, var selected: Boolean): FilterListItem()
    data class Header(override val id: String, override val name: String): FilterListItem()

    abstract val id: String
    abstract val name: String
}

data class Filter(
    val id: String,
    val name: String,
    val description: String
)