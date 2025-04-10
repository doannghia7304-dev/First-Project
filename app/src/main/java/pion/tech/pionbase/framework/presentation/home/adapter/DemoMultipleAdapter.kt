package pion.tech.pionbase.framework.presentation.home.adapter

import androidx.databinding.ViewDataBinding
import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemDummy2Binding
import pion.tech.pionbase.databinding.ItemDummyBinding
import pion.tech.pionbase.framework.presentation.common.BaseListAdapter
import pion.tech.pionbase.framework.presentation.common.createDiffCallback

class DemoMultipleAdapter : BaseListAdapter<String, ViewDataBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
        areContentsTheSame = { oldItem, newItem -> false }
    )
) {
    private val VIEW_TYPE_1 = 1
    private val VIEW_TYPE_2 = 2
    override fun getItemViewType(position: Int): Int {
        if (position % 2 == 0) {
            return VIEW_TYPE_1
        }
        return VIEW_TYPE_2
    }

    override fun getLayoutRes(viewType: Int): Int =
        if (viewType == VIEW_TYPE_1) R.layout.item_dummy else R.layout.item_dummy_2

    override fun bindView(binding: ViewDataBinding, item: String, position: Int) {

        if (binding is ItemDummyBinding) {
            binding.name.text = item
        }
        if (binding is ItemDummy2Binding) {
            binding.name.text = item
        }
    }


}
