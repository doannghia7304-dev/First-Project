package pion.tech.pionbase.feature.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.databinding.ItemSearchHistoryBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class SearchHistoryAdapter : BaseListAdapter<String, ItemSearchHistoryBinding>(
    DiffCallback()
) {

    interface Listener {
        fun onHistoryItemClick(query: String)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ItemSearchHistoryBinding {
        return ItemSearchHistoryBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemSearchHistoryBinding, item: String, position: Int) {
        binding.tvHistoryQuery.text = item
        binding.root.setPreventDoubleClick {
            listener?.onHistoryItemClick(item)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}
