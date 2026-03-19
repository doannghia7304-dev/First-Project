package pion.tech.pionbase.util

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.SystemClock
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.lang.Exception

@SuppressLint("ClickableViewAccessibility")
fun SwitchCompat.preventDrag() {
    setOnTouchListener { _, event ->
        event.action == MotionEvent.ACTION_MOVE
    }
}

fun View.setBackgroundTint(color: Int) {
    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(color))
}

fun View.removeBackgroundTint() {
    ViewCompat.setBackgroundTintList(this, null)
}

fun Context.getActionBarHeight(): Int {
    val tv = TypedValue()
    if (this.theme?.resolveAttribute(
            R.attr.actionBarSize,
            tv,
            true,
        ) == true
    ) {
        return TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    }
    return 0
}

fun View.changeBackgroundColor(newColor: Int) {
    setBackgroundColor(
        ContextCompat.getColor(
            context,
            newColor,
        ),
    )
}

fun ImageView.setTintColor(
    @ColorRes color: Int,
) {
    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
}

fun View.animRotation() {
    val anim =
        RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
        )
    anim.interpolator = LinearInterpolator()
    anim.duration = 1500
    anim.isFillEnabled = true
    anim.repeatCount = Animation.INFINITE
    anim.fillAfter = true
    startAnimation(anim)
}

fun View.setPreventDoubleClick(
    debounceTime: Long = 500,
    action: () -> Unit,
) {
    this.setOnClickListener(
        object : View.OnClickListener {
            private var lastClickTime: Long = 0

            override fun onClick(v: View?) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                action()
                lastClickTime = SystemClock.elapsedRealtime()
            }
        },
    )
}

fun View.setPreventDoubleClickScaleView(
    debounceTime: Long = 500,
    action: () -> Unit,
) {
    setOnTouchListener(
        object : View.OnTouchListener {
            private var lastClickTime: Long = 0
            private var rect: Rect? = null

            override fun onTouch(
                v: View,
                event: MotionEvent,
            ): Boolean {
                fun setScale(scale: Float) {
                    v.scaleX = scale
                    v.scaleY = scale
                }

                if (event.action == MotionEvent.ACTION_DOWN) {
                    // action down: scale view down
                    rect = Rect(v.left, v.top, v.right, v.bottom)
                    setScale(0.9f)
                } else if (rect != null &&
                    !rect!!.contains(
                        v.left + event.x.toInt(),
                        v.top + event.y.toInt(),
                    )
                ) {
                    // action moved out
                    setScale(1f)
                    return false
                } else if (event.action == MotionEvent.ACTION_UP) {
                    // action up
                    setScale(1f)
                    // handle click too fast
                    if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) {
                    } else {
                        lastClickTime = SystemClock.elapsedRealtime()
                        action()
                    }
                } else {
                    // other
                }

                return true
            }
        },
    )
}

fun Fragment.displayToast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.displayToast(
    @StringRes msg: Int,
) {
    Toast.makeText(context, getString(msg), Toast.LENGTH_SHORT).show()
}

fun Fragment.convertDpToPx(dp: Int): Int {
    val dip = dp.toFloat()
    return TypedValue
        .applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics,
        ).toInt()
}

fun Context.convertDpToPx(dp: Int): Int {
    val dip = dp.toFloat()
    return TypedValue
        .applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics,
        ).toInt()
}

fun Context.haveNetworkConnection(): Boolean {
    return try {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        return try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.allNetworkInfo
            for (ni in netInfo) {
                if (ni.typeName
                        .equals("WIFI", ignoreCase = true)
                ) {
                    if (ni.isConnected) {
                        haveConnectedWifi = true
                    }
                }
                if (ni.typeName
                        .equals("MOBILE", ignoreCase = true)
                ) {
                    if (ni.isConnected) {
                        haveConnectedMobile = true
                    }
                }
            }
            haveConnectedWifi || haveConnectedMobile
        } catch (e: Exception) {
            System.err.println(e.toString())
            false
        }
    } catch (e: Exception) {
        System.err.println(e.toString())
        false
    }
}

fun Context.openBrowser(url: String) {
    var url = url
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "http://$url"
    }
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        startActivity(browserIntent)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun ImageView.loadImage(
    source: Any?,
    placeholder: Int,
) {
    Glide
        .with(this)
        .load(source)
        .placeholder(placeholder)
        .into(this)
}

fun ImageView.loadImage(source: Any?) {
    Glide
        .with(this)
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

    Glide
        .with(this)
        .load(data)
        .listener(
            object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    onSuccess?.invoke()
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    onError?.invoke()
                    return false
                }
            },
        ).into(this)
}

fun TextView.setTextGradient(vararg colors: String) {
    if (colors.isNotEmpty()) {
        // Set the text color with the first gradient color as fallback
        setTextColor(colors[0].toColorInt())
    }
    post {
        val colorInts = colors.map { it.toColorInt() }.toIntArray()
        val shader =
            LinearGradient(
                0f,
                0f,
                width.toFloat(),
                0f,
                colorInts,
                null,
                Shader.TileMode.CLAMP,
            )
        paint.shader = shader
        postInvalidate()
    }
}
