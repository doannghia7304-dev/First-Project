package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.template.TemplateUIModel
import pion.tech.pionbase.databinding.ItemTemplateBinding
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClick

class TemplateAdapter :
    BaseListAdapter<TemplateUIModel, ItemTemplateBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.thumbnail == newItem.thumbnail },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    interface Listener {
        fun onClickTemplate(item: TemplateUIModel, position: Int)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTemplateBinding {
        return ItemTemplateBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemTemplateBinding,
        item: TemplateUIModel,
        position: Int,
    ) {
        // Sử dụng Glide tích hợp sẵn qua extension loadImage
        binding.ivTemplate.loadImage(item.thumbnail ?: item.imageModel)
        
        binding.root.setPreventDoubleClick {
            listener?.onClickTemplate(item, position)
        }
    }
}
