package pro.devonics.push

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import pro.devonics.push.DataHelper.Companion.createTransition
import pro.devonics.push.DataHelper.Companion.startTime
import pro.devonics.push.model.PushData
import pro.devonics.push.model.TimeData
import pro.devonics.push.network.ApiHelper
import pro.devonics.push.network.RetrofitBuilder
import java.util.*


private const val TAG = "PushDevonics"

class PushDevonics(context: Context, appId: String)
    : LifecycleEventObserver, Application.ActivityLifecycleCallbacks {

    private val service = ApiHelper(RetrofitBuilder.apiService)
    private val helperCache = HelperCache(context)
    private val myContext = context

    init {
        AppContextKeeper.setContext(context)
        PushInitialization.run(appId)
        startTime()
        startSession()
        createInternalId()
        openUrl(context)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d(TAG, "onStateChanged: source = $source")
        when (event) {
            Lifecycle.Event.ON_CREATE -> Log.d(TAG, "ON_CREATE: ")
            Lifecycle.Event.ON_START -> Log.d(TAG, "ON_START: ")
            Lifecycle.Event.ON_RESUME -> sendTransition()//Log.d(TAG, "onResume: ")
            Lifecycle.Event.ON_PAUSE -> openUrl(myContext)
            Lifecycle.Event.ON_STOP -> Log.d(TAG, "onStop: ")
            Lifecycle.Event.ON_DESTROY -> stopSession()//Log.d(TAG, "onDestroy: ")
        }
    }

    private fun sendTransition() {

        //Log.d(TAG, "sendTransition: clicTransition = ${helperCache.getTransitionSt()}")
        val sentPushId = helperCache.getSentPushId()
        if (sentPushId != "" || sentPushId != null) {
            val pushData = sentPushId?.let { PushData(it) }
            if (pushData != null) {
                createTransition(pushData)
            }
            Log.d(TAG, "sendTransition: pushData = $pushData")
        }
        helperCache.saveSentPushId(null)
    }

    /*fun sendIntent(intent: Intent) {

        if ("transition" == intent.getStringExtra("command")) {
            val bundle = intent.extras
            //val pushType = bundle?.get("push_type").toString()
            //val pushId = bundle?.get("push_id").toString()
            val sentPushId = bundle?.get("sent_push_id").toString()
            val pushData = PushData(sentPushId)
            createTransition(pushData)
            Log.d(TAG, "sendIntent: pushData = $pushData")
        }
    }*/

    fun openUrl(context: Context) {
        val openUrl = helperCache.getOpenUrl()

        if (openUrl != null) {
            val urlIntent = Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(openUrl))

            urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(urlIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "ActivityNotFoundException $e")
            }
        }
        helperCache.saveOpenUrl(null)
        Log.d(TAG, "openUrl = $openUrl")
    }

    fun getDeeplink(): String {
        val deep1 = helperCache.getDeeplink()
        helperCache.saveDeeplink("")
        return deep1.toString()
    }

    private fun createInternalId() {
        val pushCache = PushCache()

        var internalId = pushCache.getInternalIdFromPref()
        if (internalId == null) {
            val uuid = UUID.randomUUID()
            internalId = uuid.toString()
            pushCache.saveInternalId(internalId)
        }
    }

    fun getInternalId(): String? {
        val pushCache = PushCache()
        return pushCache.getInternalIdFromPref()
    }

    private fun startSession() {
        Log.d(TAG, "startSession: ")
        val pushCache = PushCache()
        val registrationId = pushCache.getRegistrationIdFromPref()
        if (pushCache.getSubscribeStatusFromPref() == true) {
            val session = registrationId?.let { service.createSession(it) }
            //Log.d(TAG, "subscribeStatus = ${pushCache.getSubscribeStatusFromPref()}")

        }
    }

    private fun stopSession() {
        val duration = DataHelper.getDuration()
        val pushCache = PushCache()
        val regId = pushCache.getRegistrationIdFromPref()
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

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        Log.d(TAG, "onActivityCreated()")
        startTime()
        startSession()
        createInternalId()
    }

    override fun onActivityStarted(p0: Activity) {
        Log.d(TAG, "onActivityStarted()")
    }

    override fun onActivityResumed(p0: Activity) {
        sendTransition()
        openUrl(p0)
        Log.d(TAG, "onActivityResumed()")
    }

    override fun onActivityPaused(p0: Activity) {
        Log.d(TAG, "onActivityPaused()")
    }

    override fun onActivityStopped(p0: Activity) {
        Log.d(TAG, "onActivityStopped()")
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        Log.d(TAG, "onActivitySaveInstanceState()")
    }

    override fun onActivityDestroyed(p0: Activity) {
        stopSession()
        Log.d(TAG, "onActivityDestroyed()")
    }
}