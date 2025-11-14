package pion.tech.pionbase.app

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pion.datlt.libads.AdsActivity
import pion.datlt.libads.AdsController
import pion.datlt.libads.IAPConnector
import pion.datlt.libads.utils.AdsConstant
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.base.lifecycleCallback.FragmentLifecycleCallbacksImpl
import pion.tech.pionbase.data.repository.dataStore.DataStoreRepository
import pion.tech.pionbase.util.Constant
import timber.log.Timber
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : AdsActivity() {
    private val commonViewModel: CommonViewModel by viewModels()

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

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
        Constant.isRemoteConfigSuccess = isSuccess
    }

    override fun onGetRemoteConfigDone(remoteConfig: FirebaseRemoteConfig) {
    }

    override fun getAppFlyerKey() = "4Ti9yuyaVb6BJMoy25gWUP"

    override fun onGetIapDone(isSuccess: Boolean) {
        super.onGetIapDone(isSuccess)
        val tag = "onGetIapDone"
        if (isSuccess) {
            // Ví dụ: cập nhật giao diện hoặc trạng thái ứng dụng
            val productModel =
                IAPConnector
                    .getAllProductModel()
                    .find { it.isPurchase }
            Timber.tag(tag).d("onGetIapDone: $productModel")
            if (productModel != null) {
                Constant.isPremium = productModel.isPurchase
                AdsConstant.isPremium = productModel.isPurchase
                lifecycleScope.launch(Dispatchers.IO) {
                    Constant.setPremium(
                        isPremium = productModel.isPurchase,
                        dataStoreRepository = dataStoreRepository,
                    )
                }
            } else {
                Constant.isPremium = false
                AdsConstant.isPremium = false
                lifecycleScope.launch(Dispatchers.IO) {
                    Constant.setPremium(
                        isPremium = false,
                        dataStoreRepository = dataStoreRepository,
                    )
                }
            }
        } else {
            Constant.isPremium = false
            AdsConstant.isPremium = false
            lifecycleScope.launch(Dispatchers.IO) {
                Constant.setPremium(
                    isPremium = false,
                    dataStoreRepository = dataStoreRepository,
                )
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) {
                    val outRect = Rect()
                    view.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                        runCatching {
                            val imm =
                                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                        }
                        view.clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
