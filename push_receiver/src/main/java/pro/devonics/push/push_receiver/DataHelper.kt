package pro.devonics.push.push_receiver

import android.util.Log
import pro.devonics.push.push_receiver.model.TimeData
import pro.devonics.push.push_receiver.model.Tag
import pro.devonics.push.push_receiver.network.ApiHelper
import pro.devonics.push.push_receiver.network.RetrofitBuilder
import java.util.*

private const val TAG = "Repository"

class Repository {

    companion object {

        private val service = ApiHelper(RetrofitBuilder.apiService)

        private var startTime: Long = 0
        private var stopTime: Long = 0

        private val cache = PushCache()
        val registrationId = cache.getRegistrationIdFromPref()

        fun inputTags(key: String, value: String) {
            val tag = registrationId?.let { Tag(key, value, it) }
            val saveTag = tag?.let { service.saveTag(it) }
            Log.d(TAG, "saveTags: tag = $tag")
        }

        fun sendTimeStatistic() {

            val date = Date()
            stopTime = date.time

            val duration = stopTime - startTime

            val timeData = registrationId?.let {
                TimeData(it, duration)
            }
            if (timeData != null) {
                service.sendTimeStatistic(timeData)
            }
            Log.d(TAG, "onDestroy: stopTime = $stopTime")
            Log.d(TAG, "onDestroy: duration = $duration")
        }

        fun startTime() {
            val date = Date()
            startTime = date.time
            Log.d(TAG, "onCreate: startTime = $startTime")
        }
    }
}