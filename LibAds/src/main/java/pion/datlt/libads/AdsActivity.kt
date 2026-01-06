package pion.datlt.libads

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.libiap.IapController
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import pion.datlt.libads.model.ConfigResult
import pion.datlt.libads.utils.AdsConstant
import pion.datlt.libads.utils.collectFlowOnView
import pion.datlt.libads.utils.getRemoteConfigDefaults
import pion.datlt.libads.utils.loadAndShowConsentFormIfRequire
import pion.datlt.libads.utils.requestConsentInfoUpdate

abstract class AdsActivity : AppCompatActivity() {
    protected val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private var jobSetBlockAds: Job? = null
    private var jobShowNotification: Job? = null
    private var lastTimeShowNotification: Long = 0

    override fun onResume() {
        super.onResume()
        getAppResumeInfo().let { appResumeInfo ->
            val config: Boolean =
                if (appResumeInfo != null) {
                    AdsConstant.listConfigAds[appResumeInfo.first]?.isOn ?: false
                } else {
                    false
                }
            val isInNotShowFragment: Boolean
            getListNotShowAppResumeFragmentId().let { listNotShowAppResumeFragmentId ->
                isInNotShowFragment =
                    listNotShowAppResumeFragmentId?.contains(getNavHost().currentDestination?.id)
                        ?: false
            }
            if (isInNotShowFragment || !config || AdsConstant.isPremium) {
                AdsController.isBlockOpenAds = true
            } else {
                jobSetBlockAds =
                    launchIO {
                        delay(1000L)
                        if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                            AdsController.isBlockOpenAds = false
                        }
                    }
            }
        }
        jobShowNotification?.cancel()
    }

    override fun onStop() {
        super.onStop()
        jobSetBlockAds?.cancel()
    }

    private fun launchIO(
        exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> },
        block: suspend CoroutineScope.() -> Unit,
    ): Job = lifecycleScope.launch(Dispatchers.IO + exceptionHandler, block = block)

    private fun launchDefault(
        exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> },
        block: suspend CoroutineScope.() -> Unit,
    ): Job = lifecycleScope.launch(Dispatchers.Default + exceptionHandler, block = block)

    private fun launchMain(
        exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> },
        block: suspend CoroutineScope.() -> Unit,
    ): Job = lifecycleScope.launch(Dispatchers.Main + exceptionHandler, block = block)

    protected fun initAds() {
        subscribeObserver()
        initSdk()
        initRemoteConfig()
        initIap()
        initGdpr()
    }

    private fun subscribeObserver() {
        initSdkStateFlow.collectFlowOnView(this) {
            when (it) {
                InitSdkState.NONE -> {
                }

                InitSdkState.LOADING -> {
                    initAdsStateFlow.value = InitAdsState.LOADING(InitAdsLog.START_INIT_SDK)
                }

                InitSdkState.DONE -> {
                    initAdsStateFlow.value = InitAdsState.LOADING(InitAdsLog.INIT_SDK_DONE)
                }
            }
        }
        getDataRemoteConfigStateFlow.collectFlowOnView(this) {
            when (it) {
                GetDataRemoteConfigState.NONE -> {
                }

                GetDataRemoteConfigState.LOADING -> {
                    initAdsStateFlow.value =
                        InitAdsState.LOADING(InitAdsLog.START_INIT_REMOTE_CONFIG)
                }

                GetDataRemoteConfigState.DONE -> {
                    initAdsStateFlow.value =
                        InitAdsState.LOADING(InitAdsLog.INIT_REMOTE_CONFIG_DONE)
                }
            }
        }
        initGDPRStateFlow.collectFlowOnView(this) {
            when (it) {
                InitGDPRState.NONE -> {
                }

                InitGDPRState.LOADING -> {
                    initAdsStateFlow.value = InitAdsState.LOADING(InitAdsLog.START_INIT_GDPR)
                }

                InitGDPRState.DONE -> {
                    initAdsStateFlow.value = InitAdsState.LOADING(InitAdsLog.INIT_GDPR_DONE)
                }
            }
        }
        initIAPStateFlow.collectFlowOnView(this) {
            when (it) {
                InitIAPState.NONE -> {
                }

                InitIAPState.LOADING -> {
                    initAdsStateFlow.value = InitAdsState.LOADING(InitAdsLog.START_INIT_IAP)
                }

                InitIAPState.DONE -> {
                    initAdsStateFlow.value = InitAdsState.LOADING(InitAdsLog.INIT_IAP_DONE)
                }
            }
        }
        combine(
            initSdkStateFlow,
            getDataRemoteConfigStateFlow,
            initGDPRStateFlow,
            initIAPStateFlow,
        ) { initSdkState, getDataRemoteConfigState, initGDPRState, initIAPState ->
            if (initSdkState is InitSdkState.DONE &&
                getDataRemoteConfigState is GetDataRemoteConfigState.DONE &&
                initGDPRState is InitGDPRState.DONE &&
                initIAPState is InitIAPState.DONE
            ) {
                initAdsStateFlow.value = InitAdsState.DONE
                isAllInitDone.value = true
                initNativeAfterInter()
                initAppResumeAds()
                initFaceBookSdk()
                initNotification()
            }
        }.launchIn(lifecycleScope)
    }

    abstract fun getListAppId(): List<String>

    abstract fun getNavHost(): NavController

    open fun onGetRemoteConfigDone(remoteConfig: FirebaseRemoteConfig) {
        // do nothing
    }

    open fun getNativeAfterInterInfo(): Pair<String, List<String>>? = null

    open fun getAppResumeInfo(): Pair<String, List<String>>? = null

    open fun getListNotShowAppResumeFragmentId(): List<Int>? = null

    open fun onGetIapDone(isSuccess: Boolean) {
        // do nothing
    }

    open fun onRemoteConfigSuccess(isSuccess: Boolean) {
        // Override this method in subclass to handle remote config success status
    }

    open fun getAppFlyerKey(): String? = null

    open fun getTapjoyKey(): String? = null

    open fun isDebugAds(): Boolean? = null

    private fun initSdk() {
        initSdkStateFlow.value = InitSdkState.LOADING
        val isDebug =
            if (!BuildConfig.DEBUG) {
                false
            } else {
                isDebugAds() ?: BuildConfig.DEBUG
            }
        AdsController.init(
            activity = this,
            isDebug = isDebug,
            listAppId = getListAppId(),
            appFlyerKey = getAppFlyerKey(),
            tapjoyKey = getTapjoyKey(),
            packageName = packageName,
            navController = getNavHost(),
        )
        initSdkStateFlow.value = InitSdkState.DONE
    }

    private fun initRemoteConfig() {
        getDataRemoteConfigStateFlow.value = GetDataRemoteConfigState.LOADING
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        var isTimeOut = false
        val handler = Handler(Looper.getMainLooper())
        val timeoutRunnable =
            Runnable {
                isTimeOut = true
                getDataRemoteConfig()
            }
        handler.postDelayed(timeoutRunnable, 7000L)
        val configSettings =
            remoteConfigSettings {
                minimumFetchIntervalInSeconds =
                    if (BuildConfig.DEBUG) {
                        30
                    } else {
                        3600
                    }
            }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(getRemoteConfigDefaults(context = applicationContext))
        remoteConfig
            .fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (!isTimeOut) {
                    handler.removeCallbacks(timeoutRunnable)
                }
                checkRemoteConfigSuccess(task)
                getDataRemoteConfig()
            }
    }

    private fun checkRemoteConfigSuccess(task: com.google.android.gms.tasks.Task<Boolean>) {
        if (task.isSuccessful) {
            onRemoteConfigSuccess(true)
        } else {
            onRemoteConfigSuccess(false)
        }
    }

    private fun getDataRemoteConfig() {
        onGetRemoteConfigDone(remoteConfig)
        val configShowAds = remoteConfig.getString("config_show_ads")
        kotlin.runCatching { setConfigAds(configShowAds) }
        val admobId = remoteConfig.getString("admob_id")
        kotlin.runCatching {
            AdsController.getInstance().setListAdsData(listJsonData = arrayListOf(admobId))
        }
        getDataRemoteConfigStateFlow.value = GetDataRemoteConfigState.DONE
    }

    private fun initIap() {
        initIAPStateFlow.value = InitIAPState.LOADING
        lifecycleScope.launch(Dispatchers.IO) {
            val isInitSuccess =
                try {
                    withTimeoutOrNull(5_000L) {
                        IapController.initIap(
                            application = application,
                            pathJson = "iap_id.json",
                            BuildConfig.DEBUG,
                        )
                    } ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }

            withContext(Dispatchers.Main) {
                onGetIapDone(isInitSuccess)
                initIAPStateFlow.value = InitIAPState.DONE
            }
        }
    }

    private fun initGdpr() {
        initGDPRStateFlow.value = InitGDPRState.LOADING
        AdsController.getInstance().requestConsentInfoUpdate(
            onFailed = { error ->
                // vao nhu luong binh thuong
                initGDPRStateFlow.value = InitGDPRState.DONE
            },
            onSuccess = { isRequire, isConsentAvailable ->
                if (isRequire) {
                    AdsController
                        .getInstance()
                        .loadAndShowConsentFormIfRequire(
                            onConsentError = { errorConsent ->
                                // tai consent bi loi
                                initGDPRStateFlow.value = InitGDPRState.DONE
                            },
                            onConsentDone = {
                                // tai consent thanh cong
                                initGDPRStateFlow.value = InitGDPRState.DONE
                            },
                        )
                } else {
                    // quoc gia nay khong can hien consent
                    initGDPRStateFlow.value = InitGDPRState.DONE
                }
            },
        )
    }

    private fun initNativeAfterInter() {
        val nativeConfigName = getNativeAfterInterInfo()?.first
        val listNativeSpaceName = getNativeAfterInterInfo()?.second
        if (nativeConfigName != null && listNativeSpaceName != null) {
            AdsController.getInstance().initNativeInter(
                activity = this,
                configName = nativeConfigName,
                listSpaceName = listNativeSpaceName,
            )
        }
    }

    private fun initAppResumeAds() {
        val viewShowOpenApp =
            TextView(applicationContext).apply {
                id = View.generateViewId()
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                elevation =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10f,
                        resources.displayMetrics,
                    )
                typeface = ResourcesCompat.getFont(context, R.font.font_500)
                gravity = Gravity.CENTER
                text = context.getString(R.string.loading_data)
                setTextColor(ContextCompat.getColor(context, R.color.black))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                visibility = View.GONE
            }
        val rootViewGroup = (window.decorView.rootView as? ViewGroup)
        rootViewGroup?.removeView(viewShowOpenApp)
        rootViewGroup?.addView(viewShowOpenApp)

        getAppResumeInfo()?.let { appResumeInfo ->
            AdsController.getInstance().initResumeAds(
                lifecycle = lifecycle,
                configName = appResumeInfo.first,
                listSpaceName = appResumeInfo.second,
                onShowOpenApp = {
                    viewShowOpenApp.visibility = View.VISIBLE
                },
                onStartToShowOpenAds = {
                    viewShowOpenApp.visibility = View.VISIBLE
                },
                onCloseOpenApp = {
                    viewShowOpenApp.visibility = View.GONE
                },
                onPaidEvent = {
                    // do nothing
                },
            )
        }
    }

    private fun initFaceBookSdk() {
        try {
            FacebookSdk.apply {
                setAutoInitEnabled(true)
                fullyInitialize()
                setAutoLogAppEventsEnabled(true)
                setAdvertiserIDCollectionEnabled(true)

                if (BuildConfig.DEBUG) {
                    setIsDebugEnabled(BuildConfig.DEBUG)
                    addLoggingBehavior(LoggingBehavior.APP_EVENTS)
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Toast
                    .makeText(
                        applicationContext,
                        "initFaceBookSdk: ${e.message}",
                        Toast.LENGTH_LONG,
                    ).show()
            }
        }
    }

    private fun setConfigAds(dataJson: String) {
        if (dataJson.isNotEmpty()) {
            val gson = Gson()
            val objectResult = gson.fromJson(dataJson, ConfigResult::class.java)

            // remote cho tung vi tri
            AdsConstant.apply {
                positionCloseNativeAfterInter = objectResult.positionCloseNativeAfterInter
                timeDelayNative = objectResult.timeDelayNative
                disableAllConfig = objectResult.disableAllConfig
                isOpenAppOn = objectResult.isOpenAppOn
                isInterstitialOn = objectResult.isInterstitialOn
                isNativeOn = objectResult.isNativeOn
                isNativeFullScreenOn = objectResult.isNativeFullScreenOn
                isBannerOn = objectResult.isBannerOn
                isBannerAdaptiveOn = objectResult.isBannerAdaptiveOn
                isBannerLargeOn = objectResult.isBannerLargeOn
                isBannerInlineOn = objectResult.isBannerInlineOn
                isBannerCollapsibleOn = objectResult.isBannerCollapsibleOn
                isRewardVideoOn = objectResult.isRewardVideoOn
                isRewardInterOn = objectResult.isRewardInterOn

                // remote notification
//                isNotificationOn = objectResult.isNotificationOn
                notificationTemplate = objectResult.notificationTemplate
                timeShowNotificationAfterLeftApp = objectResult.timeShowNotificationAfterLeftApp
                timeDelayNotification = objectResult.timeDelayNotification
            }

            for (config in objectResult.listConfig) {
                AdsConstant.listConfigAds[config.configName] = config
            }
        }
    }

    private fun initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanelName =
                applicationContext.applicationInfo
                    .loadLabel(applicationContext.packageManager)
                    .toString()
            val descriptionText = "Channel for ads app notifications"
            val channelID = applicationContext.packageName
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(channelID, chanelName, importance).apply {
                    description = descriptionText
                    enableVibration(true)
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                        Notification.AUDIO_ATTRIBUTES_DEFAULT,
                    )
                }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification() {
        if (!checkConditionShowNotification()) return
        val intent = Intent(applicationContext, this.javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val builder =
            NotificationCompat
                .Builder(applicationContext, applicationContext.packageName)
                .setSmallIcon(R.drawable.ic_phone_ads) // icon bắt buộc (trắng đen vector)
                .setCustomContentView(getNotificationTemplate())
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            jobShowNotification?.cancel()
            jobShowNotification =
                launchIO {
                    delay(AdsConstant.timeShowNotificationAfterLeftApp)
                    lastTimeShowNotification = System.currentTimeMillis()
                    notificationManager.notify(1, builder.build())
                }
        }
    }

    private fun checkConditionShowNotification(): Boolean {
        val isOverTime =
            System.currentTimeMillis() - lastTimeShowNotification > AdsConstant.timeDelayNotification
        val isNotificationOn = AdsConstant.isNotificationOn
        return isNotificationOn && isOverTime
    }

    private fun getNotificationTemplate(): RemoteViews? {
        try {
            val remoteView =
                when (AdsConstant.notificationTemplate) {
                    "notification_with_cta" -> {
                        RemoteViews(applicationContext.packageName, R.layout.notification_with_cta)
                    }

                    "notification_no_cta2" -> {
                        RemoteViews(applicationContext.packageName, R.layout.notification_no_cta2)
                    }

                    else -> {
                        RemoteViews(applicationContext.packageName, R.layout.notification_no_cta1)
                    }
                }

            remoteView.apply {
                setTextViewText(
                    R.id.txvAppName,
                    applicationContext.applicationInfo
                        .loadLabel(applicationContext.packageManager)
                        .toString(),
                )
                setTextViewText(
                    R.id.txvBody,
                    applicationContext.getString(R.string.you_have_not_done_your_journey_click_the_noti_to_return),
                )
            }
            return remoteView
        } catch (_: Exception) {
        }
        return null
    }

    private val initSdkStateFlow = MutableStateFlow<InitSdkState>(InitSdkState.NONE)

    sealed class InitSdkState {
        data object NONE : InitSdkState()

        data object LOADING : InitSdkState()

        data object DONE : InitSdkState()
    }

    private val getDataRemoteConfigStateFlow =
        MutableStateFlow<GetDataRemoteConfigState>(GetDataRemoteConfigState.NONE)

    sealed class GetDataRemoteConfigState {
        data object NONE : GetDataRemoteConfigState()

        data object LOADING : GetDataRemoteConfigState()

        data object DONE : GetDataRemoteConfigState()
    }

    private val initIAPStateFlow = MutableStateFlow<InitIAPState>(InitIAPState.NONE)

    sealed class InitIAPState {
        data object NONE : InitIAPState()

        data object LOADING : InitIAPState()

        data object DONE : InitIAPState()
    }

    private val initGDPRStateFlow = MutableStateFlow<InitGDPRState>(InitGDPRState.NONE)
    val isAllInitDone = MutableStateFlow(false)

    sealed class InitGDPRState {
        data object NONE : InitGDPRState()

        data object LOADING : InitGDPRState()

        data object DONE : InitGDPRState()
    }

    val initAdsStateFlow = MutableStateFlow<InitAdsState>(InitAdsState.NONE)

    sealed class InitAdsState {
        data object NONE : InitAdsState()

        data class LOADING(
            val log: InitAdsLog,
        ) : InitAdsState()

        data object DONE : InitAdsState()
    }

    enum class InitAdsLog {
        START_INIT_SDK,
        INIT_SDK_DONE,
        START_INIT_IAP,
        INIT_IAP_DONE,
        START_INIT_GDPR,
        INIT_GDPR_DONE,
        START_INIT_REMOTE_CONFIG,
        INIT_REMOTE_CONFIG_DONE,
    }
}
