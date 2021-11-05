package pro.devonics.push

import pro.devonics.push.model.Tag
import pro.devonics.push.model.TimeData
import pro.devonics.push.network.ApiHelper
import pro.devonics.push.network.RetrofitBuilder
import java.util.*

private const val TAG = "Repository"

class DataHelper {

    companion object {

        private val service = ApiHelper(RetrofitBuilder.apiService)

        private var startTime: Long = 0
        private var stopTime: Long = 0

        private val cache = PushCache()
        val registrationId = cache.getRegistrationIdFromPref()

        fun inputTags(key: String, value: String) {
            val tag = registrationId?.let { Tag(key, value, it) }
            val saveTag = tag?.let { service.saveTag(it) }
            //Log.d(TAG, "saveTags: tag = $tag")
        }

        fun sendTimeStatistic() {
            val date = Calendar.getInstance().timeInMillis
            stopTime = date

            val duration = (stopTime - startTime) / 1000

            val timeData = registrationId?.let {
                TimeData(it, duration)
            }
            if (timeData != null) {
                service.sendTimeStatistic(timeData)
            }
            //Log.d(TAG, "onDestroy: stopTime = $stopTime")
            //Log.d(TAG, "onDestroy: duration = $duration")
        }

        fun startTime() {
            val date = Calendar.getInstance().timeInMillis
            startTime = date
            //Log.d(TAG, "onCreate: startTime = $startTime")
        }

        fun createTransition() {
            val transition = registrationId?.let { service.createTransition(it) }
            //Log.d(TAG, "createTransition: = $transition")
        }
    }
}