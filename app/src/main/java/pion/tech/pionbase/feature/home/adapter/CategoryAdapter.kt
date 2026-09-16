package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.appCategory.AppCategoryUIModel
import pion.tech.pionbase.databinding.ItemCategoryBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class CategoryAdapter :
    BaseListAdapter<AppCategoryUIModel, ItemCategoryBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    interface Listener {
        fun onClickCategory(item: AppCategoryUIModel, position: Int)
    }

    private var listener: Listener? = null
    private var selectedPosition = 0

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemCategoryBinding {
        return ItemCategoryBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemCategoryBinding,
        item: AppCategoryUIModel,
        position: Int,
    ) {
        binding.tvCategoryName.text = item.name
        
        val context = binding.root.context
        if (position == selectedPosition) {
            binding.tvCategoryName.setBackgroundResource(R.drawable.bg_category_selected)
            binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            binding.tvCategoryName.setBackgroundResource(R.drawable.bg_category_unselected)
            binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
        }

        binding.root.setPreventDoubleClick {
            val oldPos = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            listener?.onClickCategory(item, position)
        }
    }
}
