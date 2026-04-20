package pion.tech.pionbase.app

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseAnalyticsLogger
import pion.tech.pionbase.base.firebaseAnalytics.FirebaseEventNameSanitizer
import pion.tech.pionbase.base.lifecycleCallback.FragmentLifecycleCallbacksImpl
import pion.tech.pionbase.util.AppRemoteConfig
import pion.tech.pionbase.util.collectFlowOnView
import timber.log.Timber
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private val commonViewModel: CommonViewModel by viewModel()
    val logger: FirebaseAnalyticsLogger by inject()

    companion object {
        private const val RESTART_DELAY_MS = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            FragmentLifecycleCallbacksImpl(),
            true,
        )
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupNavigationLogging()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        commonViewModel.uiState
            .map { it.isPremium }
            .distinctUntilChanged()
            .collectFlowOnView(this) {

            }
    }

    private fun setupNavigationLogging() {
        val navHostFragment =
            supportFragmentManager
                .findFragmentById(R.id.fragmentContainerMain) as? NavHostFragment
                ?: return
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            val screenName = destination.label?.toString() ?: return@addOnDestinationChangedListener
            val sanitizedName =
                FirebaseEventNameSanitizer.sanitize(screenName)
                    ?: return@addOnDestinationChangedListener
            logger.logEvent("${sanitizedName}_show")
            logger.logScreen("${sanitizedName}_view")
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            hideKeyboardOnTouchOutsideEditText(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboardOnTouchOutsideEditText(ev: MotionEvent) {
        val view = currentFocus as? EditText ?: return
        val outRect = Rect()
        view.getGlobalVisibleRect(outRect)
        if (outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) return
        runCatching {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        view.clearFocus()
    }
}
