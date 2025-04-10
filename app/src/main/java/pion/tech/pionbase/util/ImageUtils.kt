package pion.tech.pionbase.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File

fun ImageView.loadImage(
    source: Any?,
    placeholder: Int
) {
    Glide.with(this)
        .load(source)
        .placeholder(placeholder)
        .into(this)
}

fun ImageView.loadImage(
    source: Any?,
) {
    Glide.with(this)
        .load(source)
        .into(this)
}

fun ImageView.loadWithCallback(
    data: Any?,
    onStart: (() -> Unit)? = null,
    onSuccess: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
) {
    onStart?.invoke()

    Glide.with(this)
        .load(data)
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                onSuccess?.invoke()
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                onError?.invoke()
                return false
            }
        })
        .into(this)
}


