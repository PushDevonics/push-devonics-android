package pro.devonics.push.push_receiver

import android.content.Context
import android.util.Log

private const val TAG = "PushCache"

class PushCache {

    private val PUSH_CACHE = "push_cache"
    private val OLD_REGISTRATION_ID = "old_registration_id"
    private val REGISTRATION_ID = "registration_id"
    private val REGISTRATION_STATUS = "registration_status"

    private val appContext = AppContextKeeper.getContext()
    private val ed = appContext
        .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
        ?.edit()

    fun saveOldRegistrationIdPref(old_reg_id: String) {
        ed?.putString(OLD_REGISTRATION_ID, old_reg_id)
        Log.d(TAG, "aveOldRegistrationIdPref: old_reg_id = $old_reg_id")
        ed?.apply()
    }

    fun getOldRegistrationIdFromPref(): String? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getString(OLD_REGISTRATION_ID, null)
    }

    fun saveRegistrationIdPref(reg_id: String) {
        ed?.putString(REGISTRATION_ID, reg_id)
        Log.d(TAG, "saveRegistrationIdPref: reg_id = $reg_id")
        ed?.apply()
    }

    fun getRegistrationIdFromPref(): String? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getString(REGISTRATION_ID, null)
    }

    fun saveRegistrationStatus(status: Int) {
        ed?.putInt(REGISTRATION_STATUS, status)
        Log.d(TAG, "saveRegistrationStatus: status = $status")
        ed?.apply()
    }

    fun getRegistrationStatusFromPref(): Int? {
        return appContext
            .getSharedPreferences(PUSH_CACHE, Context.MODE_PRIVATE)
            ?.getInt(REGISTRATION_STATUS, 0)
    }
}