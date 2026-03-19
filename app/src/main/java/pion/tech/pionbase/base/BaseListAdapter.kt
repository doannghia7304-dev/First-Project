package pion.tech.pionbase.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Shared singleton executor for all ListAdapter diff calculations.
 * Prevents thread leak from creating new executor per adapter.
 */
private val diffExecutor: ExecutorService by lazy {
    Executors.newSingleThreadExecutor { r ->
        Thread(r, "ListAdapter-Diff-Thread").apply {
            isDaemon = true // Don't prevent JVM shutdown
        }
    }
}

private interface BaseRecyclerAdapter<Item : Any, ViewBinding : ViewDataBinding> {
    /**
     * get layout res based on view type
     */
    fun getLayoutRes(viewType: Int): Int

    /**
     * bind view
     */
    fun bindView(
        binding: ViewBinding,
        item: Item,
        position: Int,
    )
}

/**
 * base recycler view adapter
 */
abstract class BaseListAdapter<Item : Any, ViewBinding : ViewDataBinding>(
    diffCallback: DiffUtil.ItemCallback<Item>,
) : ListAdapter<Item, BaseViewHolder<ViewBinding>>(
        AsyncDifferConfig
            .Builder(diffCallback)
            .setBackgroundThreadExecutor(diffExecutor)
            .build(),
    ),
    BaseRecyclerAdapter<Item, ViewBinding> {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<ViewBinding> {
        val binding: ViewBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                getLayoutRes(viewType),
                parent,
                false,
            )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ViewBinding>,
        position: Int,
    ) {
        val item = getItem(position)
        if (item != null) {
            bindView(holder.binding, item, position)
        }
        holder.binding.executePendingBindings()
    }
}

open class BaseViewHolder<ViewBinding : ViewDataBinding>(
    val binding: ViewBinding,
) : RecyclerView.ViewHolder(binding.root)

inline fun <T> createDiffCallback(
    crossinline areItemsTheSame: (T, T) -> Boolean,
    crossinline areContentsTheSame: (T, T) -> Boolean,
): DiffUtil.ItemCallback<T> =
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(
            oldItem: T & Any,
            newItem: T & Any,
        ): Boolean = areItemsTheSame(oldItem, newItem)

        override fun areContentsTheSame(
            oldItem: T & Any,
            newItem: T & Any,
        ): Boolean = areContentsTheSame(oldItem, newItem)
    }
