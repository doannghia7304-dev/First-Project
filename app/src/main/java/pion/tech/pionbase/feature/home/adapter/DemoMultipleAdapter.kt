package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.databinding.ItemDummy2Binding
import pion.tech.pionbase.databinding.ItemDummyBinding

class DemoMultipleAdapter :
    BaseListAdapter<String, ViewBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
            areContentsTheSame = { oldItem, newItem -> false },
        ),
    ) {
    private val VIEW_TYPE_1 = 1
    private val VIEW_TYPE_2 = 2

    override fun getItemViewType(position: Int): Int {
        if (position % 2 == 0) {
            return VIEW_TYPE_1
        }
        return VIEW_TYPE_2
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return if (viewType == VIEW_TYPE_1) {
            ItemDummyBinding.inflate(inflater, parent, false)
        } else {
            ItemDummy2Binding.inflate(inflater, parent, false)
        }
    }

    override fun bindView(
        binding: ViewBinding,
        item: String,
        position: Int,
    ) {
        if (binding is ItemDummyBinding) {
            binding.name.text = item
        }
        if (binding is ItemDummy2Binding) {
            binding.name.text = item
        }
    }
}
