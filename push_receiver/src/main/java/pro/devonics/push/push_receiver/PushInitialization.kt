package pro.devonics.push.push_receiver

import android.os.Build
import android.util.Log
import pro.devonics.push.push_receiver.network.RetrofitBuilder
import pro.devonics.push.push_receiver.model.PushInstance
import pro.devonics.push.push_receiver.model.PushUser
import pro.devonics.push.push_receiver.network.ApiHelper
import java.util.*

private const val TAG = "PushInitialization"

class PushInitialization {

    companion object {

        fun run(appId: String) {

            val appContext = AppContextKeeper.getContext()
            val service = ApiHelper(RetrofitBuilder.apiService)

            val thread = Thread {

                val sender = service.getSenderData(appId)
                Log.d(TAG, "run: senderId = ${sender?.getSenderId()}")
                val pushRegistratorFCM = PushRegistratorFCM()
                if (sender != null) {
                    pushRegistratorFCM.registerForPush(
                        appContext,
                        sender.getSenderId(),
                        object : PushRegistrator.RegisteredHandler {
                            override fun complete(registrationId: String?, status: Int) {
                                Log.d(TAG, "complete: registrationId = $registrationId}")
                                Log.d(TAG, "complete: status = $status}")
                                val pushCache = PushCache()
                                val regId = pushCache.getRegistrationIdFromPref()

                                if (!regId.equals(registrationId)) {
                                    if (registrationId != null) {
                                        val pushInstance =
                                            regId?.let { PushInstance(it, registrationId) }
                                        val updatedRegistrationsId = pushInstance?.let {
                                            service.updateRegistrationId(
                                                it
                                            )
                                        }
                                        val session = service.createSession(registrationId)
                                        pushCache.saveRegistrationIdPref(registrationId)
                                        Log.d(TAG, "complete2: session = $session")
                                        Log.d(TAG, "complete2: pushInstance = $pushInstance")
                                        Log.d(TAG, "complete2: updatedRegistrationsId = $updatedRegistrationsId")
                                    }

                                    Log.d(TAG, "complete: !regId.equals(registrationId)")
                                    //Log.d(TAG, "complete: registrationId = $registrationId")
                                }

                                if (registrationId != null) {
                                    if (regId == null) {
                                        val pushUser = setPushUser(registrationId, appId)
                                        val subscribe = service.createPush(pushUser)
                                        Log.d(TAG, "complete: pushUser = $pushUser")
                                        Log.d(TAG, "complete: subscribe = $subscribe")
                                    }

                                    val session = service.createSession(registrationId)
                                    Log.d(TAG, "complete: session = $session")
                                }
                                if (status > 0) {
                                    pushCache.saveRegistrationStatus(status)
                                }
                            }
                        }
                    )
                }
            }
            thread.start()
        }

        private fun checkRegistrationId(registrationId: String?) {
            val pushCache = PushCache()
            val regId = pushCache.getRegistrationIdFromPref()
            val oldRegId = pushCache.getOldRegistrationIdFromPref()

            if (registrationId != null) {
                if (regId == null) {
                    pushCache.saveRegistrationIdPref(registrationId)
                }
            }
        }

        private fun isValidRegistrationId(regId: String?, oldRegId: String?): Boolean {
            var isValid = false

            if (regId != null && oldRegId != null) {
                if (regId == oldRegId) {
                    isValid = true
                }
            }
            return isValid
        }

        private fun setPushUser(registrationId: String, appId: String): PushUser {

            //Get timezone
            val tz = TimeZone.getDefault()//.toZoneId()
            val timezone = tz.id
            //Log.d(TAG, "complete: timezone = $timezone")

            //Get language
            val locale = Locale.getDefault()
            val lang = locale.language
            //Log.d(TAG, "complete: lang = $lang")

            //Get country
            val country = Locale("", locale.country).country
                //.getDisplayCountry(Locale("EN"))
            //Log.d(TAG, "complete: country = $country")

            //Get device info
            val deviceInfo = getDeviceData()
            //Log.d(TAG, "complete: deviceInfo = $deviceInfo")

            return PushUser(
                registrationId,
                appId,
                "Android",
                country,
                lang,
                timezone,
                deviceInfo
            )
        }

        private fun getDeviceData(): String {
            //val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val brand = Build.BRAND
            //val product = Build.PRODUCT

            return "$brand/$model"
        }
    }
}