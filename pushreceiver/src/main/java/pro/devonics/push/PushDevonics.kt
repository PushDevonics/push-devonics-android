package pro.devonics.push

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import pro.devonics.push.DataHelper.Companion.createTransition
import pro.devonics.push.DataHelper.Companion.inputTags
import pro.devonics.push.DataHelper.Companion.sendTimeStatistic
import pro.devonics.push.DataHelper.Companion.startTime
import pro.devonics.push.model.PushData


private const val TAG = "PushDevonics"

class PushDevonics(context: Context, appId: String) : LifecycleObserver {

    init {
        AppContextKeeper.setContext(context)
        PushInitialization.run(appId)
        startTime()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> Log.d(TAG, "onCreate: ")
            Lifecycle.Event.ON_RESUME -> Log.d(TAG, "onResume: ")
            Lifecycle.Event.ON_STOP -> Log.d(TAG, "onStop: ")
            Lifecycle.Event.ON_DESTROY -> sendTimeStatistic()//Log.d(TAG, "onDestroy: ")
        }
    }

    fun sendIntent(intent: Intent) {
        val pushData = PushData("App\\Model\\CustomPush", "6")
        //Log.d(TAG, "sendIntent: intent = $intent")
        if ("transition" == intent.getStringExtra("command")) {
            createTransition(pushData)
        }
    }

    fun getInternalId(): String? {
        val pushCache = PushCache()
        val internalId = pushCache.getInternalIdFromPref()
        var i: String? = null
        Log.d(TAG, "internalId = $internalId")
        if (internalId != null) {
            i = internalId
            //Log.d(TAG, "getInternalId: i $i")
        }
        return i
    }

    fun setTags(key: String, value: String) {
        inputTags(key, value)
    }
}