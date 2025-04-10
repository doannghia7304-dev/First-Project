package pion.tech.pionbase.framework.presentation.home.adapter

import pion.tech.pionbase.R
import pion.tech.pionbase.databinding.ItemDummyBinding
import pion.tech.pionbase.framework.presentation.common.BaseListAdapter
import pion.tech.pionbase.framework.presentation.common.createDiffCallback

class DemoAdapter : BaseListAdapter<String, ItemDummyBinding>(
    createDiffCallback(
        areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
        areContentsTheSame = { oldItem, newItem -> false }
    )
) {
    override fun getLayoutRes(viewType: Int): Int = R.layout.item_dummy

    override fun bindView(binding: ItemDummyBinding, item: String, position: Int) {
        binding.name.text = item
    }

}
