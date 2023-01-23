package pro.devonics.push

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import okhttp3.logging.HttpLoggingInterceptor
import pro.devonics.push.DataHelper.Companion.startTime
import pro.devonics.push.model.PushData
import pro.devonics.push.model.TimeData
import pro.devonics.push.network.ApiHelper
import pro.devonics.push.network.RetrofitBuilder
import java.util.*


private const val TAG = "PushDevonics"
private const val PERMISSIONS_REQUEST_CODE = 2

enum class Level {
    BASIC, BODY, HEADERS, NONE
}

class PushDevonics(activity: Activity, appId: String) : LifecycleEventObserver {

    private val service = ApiHelper(RetrofitBuilder.apiService)
    private val helperCache = HelperCache(activity)
    private val myContext = activity
    private var sentPushId: String? = null
    private val mAppId = appId

    init {
        setLogLevelHttp(Level.BASIC)
        AppContextKeeper.setContext(activity)
        createInternalId()
    }

    private fun checkPermission(appId: String, activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // allow permission
                PushInit.run(appId, service)
                startTime()
                startSession(appId)
                Log.d(TAG, "Allow permission")
                // FCM SDK (and your app) can post notifications.
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                // permission denied
                Log.d(TAG, "Permission denied")
            } else {
                // ask permission
                Log.d(TAG, "Ask permission")
                activity.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSIONS_REQUEST_CODE
                )
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    PushInit.run(appId, service)
                    startTime()
                    startSession(appId)
                }
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "Ask permission for API < TIRAMISU")
            PushInit.run(appId, service)
            startTime()
            startSession(appId)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {

                sendTransition(service)
                Log.d(TAG, "ON_CREATE:")
            }
            Lifecycle.Event.ON_START -> Log.d(TAG, "ON_START:")
            Lifecycle.Event.ON_RESUME -> {
                checkPermission(mAppId, myContext)
                openUrl()
                Log.d(TAG, "ON_RESUME:")
            }
            Lifecycle.Event.ON_PAUSE -> Log.d(TAG, "ON_PAUSE:")
            Lifecycle.Event.ON_STOP -> {
                stopSession()
                Log.d(TAG, "ON_STOP:")
            }
            Lifecycle.Event.ON_DESTROY -> Log.d(TAG, "onDestroy: ")
            else -> {}
        }
    }

    fun setLogLevelHttp(level: Level) {
        RetrofitBuilder.loggingInterceptor.apply {
            when (level) {
                Level.BASIC -> {
                    this.setLevel(HttpLoggingInterceptor.Level.BASIC)
                    Log.d(TAG, "setLogLevelHttp: BASIC")
                }
                Level.HEADERS -> {
                    Log.d(TAG, "setLogLevelHttp: HEADERS")
                    this.setLevel(HttpLoggingInterceptor.Level.HEADERS)
                }
                Level.BODY -> {
                    Log.d(TAG, "setLogLevelHttp: BODY")
                    this.setLevel(HttpLoggingInterceptor.Level.BODY)
                }
                Level.NONE -> {
                    this.setLevel(HttpLoggingInterceptor.Level.NONE)
                    Log.d(TAG, "setLogLevelHttp: NONE")
                }
            }
        }
    }

    private fun sendTransition(service: ApiHelper) {
        sentPushId = helperCache.getSentPushId()
        val pushCache = PushCache()
        val registrationId = pushCache.getRegistrationId()
        val transition = helperCache.getTransition()
        if (transition == false && sentPushId != null) {
            val pushData = sentPushId?.let { PushData(it) }
            if (pushData != null && registrationId != null) {
                service.createTransition(registrationId, pushData)
                helperCache.saveTransition(true)
            }
        }
        helperCache.saveSentPushId(null)
    }

    private fun openUrl() {
        val openUrl = helperCache.getOpenUrl()

        if (openUrl != null) {
            val urlIntent = Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(openUrl))

            urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                myContext.startActivity(urlIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "ActivityNotFoundException $e")
            }
        }
        helperCache.saveOpenUrl(null)
        //Log.d(TAG, "openUrl = $openUrl")
    }

    fun getDeeplink(): String {
        val deep1 = helperCache.getDeeplink()
        helperCache.saveDeeplink("")
        return deep1.toString()
    }

    private fun createInternalId() {
        val pushCache = PushCache()

        var internalId = pushCache.getInternalId()
        if (internalId == null) {
            internalId = UUID.randomUUID().toString()
            pushCache.saveInternalId(internalId)
        }
    }

    fun getInternalId(): String? {
        val pushCache = PushCache()
        return pushCache.getInternalId()
    }

    private fun startSession(appId: String) {
        Log.d(TAG, "startSession: ")
        val pushCache = PushCache()
        val registrationId = pushCache.getRegistrationId()
        if (pushCache.getSubscribeStatus() == true) {
            val session = registrationId?.let { service.createSession(it, appId) }
            //Log.d(TAG, "subscribeStatus = ${pushCache.getSubscribeStatusFromPref()}")

        }
    }

    private fun stopSession() {
        val duration = DataHelper.getDuration()
        val pushCache = PushCache()
        val regId = pushCache.getRegistrationId()
        if (regId != null) {
            val timeData = TimeData(duration)
            service.sendTimeStatistic(regId, timeData)
            //Log.d(TAG, "stopSession: timeData $timeData")
        }

        //Log.d(TAG, "stopSession: duration $duration")
        //Log.d(TAG, "stopSession: regId $regId")
        Log.d(TAG, "stopSession")
    }

    fun setTags(key: String, value: String) {
        val pushCache = PushCache()
        if (key == null && value == null) {
            pushCache.saveTagKey("")
            pushCache.saveTagValue("")
        } else {
            pushCache.saveTagKey(key)
            pushCache.saveTagValue(value)
        }
    }
}