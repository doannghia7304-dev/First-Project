package pion.tech.pionbase.feature.language.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.language.LanguageUIModel
import pion.tech.pionbase.databinding.ItemLanguageBinding
import pion.tech.pionbase.util.loadImage
import pion.tech.pionbase.util.setPreventDoubleClick

class LanguageAdapter :
    BaseListAdapter<LanguageUIModel, ItemLanguageBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.localeCode == newItem.localeCode },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    interface Listener {
        fun onClickLanguage(
            item: LanguageUIModel,
            position: Int,
        )
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemLanguageBinding {
        return ItemLanguageBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemLanguageBinding,
        item: LanguageUIModel,
        position: Int,
    ) {
        fun loadBackground() {
            if (item.isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_language_selected)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_language_unselected)
            }
        }

        loadBackground()
        binding.tvName.text = item.nameCountry
        binding.ivFlag.loadImage(item.thumbnail)
        binding.root.setPreventDoubleClick {
            listener?.onClickLanguage(item, position)
        }
    }
}
