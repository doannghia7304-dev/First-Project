package pion.tech.pionbase.feature.home.dialog

import android.os.Bundle
import pion.tech.pionbase.base.BaseBottomSheetDialogFragment
import pion.tech.pionbase.data.model.appCategory.AppCategoryUIModel
import pion.tech.pionbase.databinding.DialogCategoriesBinding
import pion.tech.pionbase.feature.home.adapter.CategoryAdapter

class CategoryBottomSheetDialog(
    private val categories: List<AppCategoryUIModel>,
    private val onCategorySelected: (AppCategoryUIModel) -> Unit
) : BaseBottomSheetDialogFragment<DialogCategoriesBinding>(
    DialogCategoriesBinding::inflate
), CategoryAdapter.Listener {

    private val adapter = CategoryAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        adapter.setListener(this)
        binding.rvCategoriesDialog.adapter = adapter
        adapter.submitList(categories)
    }

    override fun onClickCategory(item: AppCategoryUIModel, position: Int) {
        onCategorySelected(item)
        dismiss()
    }
}
