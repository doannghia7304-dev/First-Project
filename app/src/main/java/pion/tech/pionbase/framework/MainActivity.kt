package pion.tech.pionbase.framework

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.R
import pion.tech.pionbase.framework.presentation.common.LoadingDialog
import pion.tech.pionbase.framework.presentation.common.lifecycleCallback.FragmentLifecycleCallbacksImpl

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacksImpl(), true)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main) //TODO: change name navhost
    }

    fun showLoading() {
        LoadingDialog.getInstance().show(supportFragmentManager)
    }

    fun hiddenLoading() {
        LoadingDialog.getInstance().dismiss()
    }
}