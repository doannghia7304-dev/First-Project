package pion.tech.pionbase.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.libiap.IAPConnector
import com.example.libiap.SubscribeInterface
import com.example.libiap.model.ProductModel
import dagger.hilt.android.AndroidEntryPoint
import pion.datlt.libads.AdsController
import pion.datlt.libads.utils.AdsConstant
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.base.lifecycleCallback.FragmentLifecycleCallbacksImpl
import pion.tech.pionbase.util.Constant
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
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
    }

    private fun initAds() {
        AdsController.Companion.init(
            activity = this,
            isDebug = BuildConfig.DEBUG,
            listAppId =
                arrayListOf(
                    getString(R.string.admob_application_id),
                ),
            packageName = packageName,
            navController = getNavHost(),
        )
    }

    fun initAppResumeAds() {
        AdsController.Companion.getInstance().initResumeAds(
            lifecycle = lifecycle,
            listSpaceName = listOf("appresume_openad1", "appresume_openad2", "appresume_openad3"),
            onShowOpenApp = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = true
            },
            onStartToShowOpenAds = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = true
            },
            onCloseOpenApp = {
                findViewById<TextView>(R.id.viewShowOpenApp).isVisible = false
            },
            onPaidEvent = {
                // do nothing
            },
        )
    }

    private fun initPurchaseIap() {
        application?.let { IAPConnector.initIap(it, "iap_id.json", BuildConfig.DEBUG) }
        IAPConnector.addIAPListener(
            object : SubscribeInterface {
                override fun subscribeSuccess(productModel: ProductModel) {
                    // set lai cac bien check
                    Constant.isPremium = true
                    AdsConstant.isPremium = true
                    commonViewModel.setPremium(true)
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
                }
            },
        )
    }

    private fun getNavHost(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        return navHostFragment.navController
    }
}
