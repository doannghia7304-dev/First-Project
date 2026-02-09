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
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.android.ext.android.inject
import pion.datlt.libads.AdsActivity
import pion.datlt.libads.iap.IapController
import pion.datlt.libads.iap.SubscribeInterface
import pion.datlt.libads.iap.model.ProductModel
import pion.datlt.libads.utils.AdsConstant
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.base.lifecycleCallback.FragmentLifecycleCallbacksImpl
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.AppRemoteConfig
import pion.tech.pionbase.util.Constant
import pion.tech.pionbase.util.collectFlowOnView
import pion.tech.pionbase.util.handleUiState
import timber.log.Timber
import kotlin.getValue

class MainActivity : AdsActivity() {
    private val commonViewModel: CommonViewModel by viewModels()

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
        initAds()
        initPurchaseIap()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        commonViewModel.uiState
            .map { it.isPremium }
            .distinctUntilChanged()
            .collectFlowOnView(this) {
                AdsConstant.isPremium = it
            }
    }

    override fun getListAppId(): List<String> =
        listOf(
            getString(R.string.admob_application_id),
        )

    override fun isDebugAds() = BuildConfig.DEBUG

    override fun getNavHost(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        return navHostFragment.navController
    }

    override fun getNativeAfterInterInfo(): Pair<String, List<String>> {
        // Trả về list id của quảng cáo native sau inter
        return Pair(
            "afterinterstitial",
            listOf("afterinterstitial_native1", "afterinterstitial_native2"),
        ) // k: configName || v: list id native
    }

    override fun getAppResumeInfo(): Pair<String, List<String>> {
        // Trả về list id của quảng cáo app resume
        return Pair(
            "appresume",
            listOf("appresume_openad1", "appresume_openad2"),
        ) // k: configName || v: list id native
    }

    override fun getListNotShowAppResumeFragmentId(): List<Int> {
        // Trả về list id của các fragment không hiển thị quảng cáo app resume
        return listOf(
            R.id.splashFragment,
            R.id.onboardFragment,
        )
    }

    override fun onRemoteConfigSuccess(isSuccess: Boolean) {
        AppRemoteConfig.setRemoteConfigSuccess(isSuccess)
    }

    override fun onGetRemoteConfigDone(remoteConfig: FirebaseRemoteConfig) {
        AppRemoteConfig.init(remoteConfig)
    }

    override fun getAppFlyerKey() = "4Ti9yuyaVb6BJMoy25gWUP"

    override fun onGetIapDone(isSuccess: Boolean) {
        super.onGetIapDone(isSuccess)
        val tag = "onGetIapDone"
        if (isSuccess) {
            val productModel =
                IapController
                    .getListAllProduct()
                    .find { it.isPurchase }
            Timber.tag(tag).d("onGetIapDone: $productModel")
            if (productModel != null) {
                commonViewModel.setPremium(productModel.isPurchase)
            } else {
                commonViewModel.setPremium(false)
            }
        }
    }

    private fun initPurchaseIap() {
        IapController.setIAPListener(
            object : SubscribeInterface {
                override fun subscribeSuccess(productModel: ProductModel) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent =
                            baseContext.packageManager.getLaunchIntentForPackage(
                                baseContext.packageName,
                            )
                        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }, 500)
                }

                override fun subscribeError(error: String) {
                    Timber.e("Subscribe error: $error")
                }
            },
        )
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
