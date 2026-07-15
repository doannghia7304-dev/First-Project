package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.databinding.ItemDummyBinding

class DemoAdapter :
    BaseListAdapter<String, ItemDummyBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
            areContentsTheSame = { oldItem, newItem -> false },
        ),
    ) {
    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemDummyBinding {
        return ItemDummyBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemDummyBinding,
        item: String,
        position: Int,
    ) {
        binding.name.text = item
    }
}
