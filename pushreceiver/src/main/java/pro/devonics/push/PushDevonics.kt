package pro.devonics.push

import android.content.Context
import android.content.Intent
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

class PushDevonics(context: Context, appId: String) : LifecycleEventObserver {

    private val service = ApiHelper(RetrofitBuilder.apiService)

    init {
        AppContextKeeper.setContext(context)
        PushInitialization.run(appId)
        createInternalId()
        startTime()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> Log.d(TAG, "onCreate: ")
            Lifecycle.Event.ON_START -> startSession()
            Lifecycle.Event.ON_RESUME -> Log.d(TAG, "onResume: ")
            Lifecycle.Event.ON_STOP -> Log.d(TAG, "onStop: ")
            Lifecycle.Event.ON_DESTROY -> stopSession()//Log.d(TAG, "onDestroy: ")
        }
    }

    fun sendIntent(intent: Intent) {

        if ("transition" == intent.getStringExtra("command")) {
            val bundle = intent.extras
            val pushType = bundle?.get("push_type").toString()
            val pushId = bundle?.get("push_id").toString()

            val pushData = PushData(pushType, pushId)

            createTransition(pushData)
            Log.d(TAG, "sendIntent: pushData = $pushData")
        }
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
        var internalId = pushCache.getInternalIdFromPref()
        if (internalId == null) {
            val uuid = UUID.randomUUID()
            internalId = uuid.toString()
            pushCache.saveInternalId(internalId)
        }
        return internalId
    }

    private fun startSession() {
        Log.d(TAG, "startSession: ")
        val pushCache = PushCache()
        if (pushCache.getSubscribeStatusFromPref() == true) {
            val session = DataHelper.registrationId?.let { service.createSession(it) }
            //Log.d(TAG, "subscribeStatus = ${pushCache.getSubscribeStatusFromPref()}")

        }
    }

    private fun stopSession() {
        val duration = DataHelper.getDuration()
        val pushCache = PushCache()
        val regId = pushCache.getRegistrationIdFromPref()
        if (regId != null) {
            val timeData = TimeData(regId, duration)
            service.sendTimeStatistic(timeData)
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