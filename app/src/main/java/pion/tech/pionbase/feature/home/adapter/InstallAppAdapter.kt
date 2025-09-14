package pion.tech.pionbase.feature.home.adapter

import pion.tech.pionbase.R
import pion.tech.pionbase.base.BaseListAdapter
import pion.tech.pionbase.base.createDiffCallback
import pion.tech.pionbase.data.model.installedApp.InstalledAppUIModel
import pion.tech.pionbase.databinding.ItemAppBinding

class InstallAppAdapter :
    BaseListAdapter<InstalledAppUIModel, ItemAppBinding>(
        createDiffCallback(
            areItemsTheSame = { oldItem, newItem -> oldItem.packageName == newItem.packageName },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        ),
    ) {
    override fun getLayoutRes(viewType: Int): Int = R.layout.item_app

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
        }
    }
}