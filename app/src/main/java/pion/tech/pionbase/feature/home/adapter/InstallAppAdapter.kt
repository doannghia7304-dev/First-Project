package pion.tech.pionbase.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.databinding.ItemAppBinding
import pion.tech.pionbase.util.setPreventDoubleClick

class InstallAppAdapter :
    BaseListAdapter<InstalledAppUIModel, ItemAppBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.packageName == newItem.packageName },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    interface Listener {
        fun onClickApp(item: InstalledAppUIModel)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemAppBinding {
        return ItemAppBinding.inflate(inflater, parent, false)
    }

    override fun bindView(
        binding: ItemAppBinding,
        item: InstalledAppUIModel,
        position: Int,
    ) {
        binding.apply {
            tvAppName.text = item.appName
            tvPackageName.text = item.packageName
            tvVersionName.text = item.versionName ?: "Unknown"
            ivAppIcon.setImageDrawable(item.icon)
            root.setPreventDoubleClick {
                listener?.onClickApp(item)
            }
        }
    }
}
