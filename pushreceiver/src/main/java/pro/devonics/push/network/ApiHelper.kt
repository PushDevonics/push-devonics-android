package pro.devonics.push.network

import android.util.Log
import pro.devonics.push.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private const val TAG = "ApiHelper"

class ApiHelper(private val apiService: ApiService) {

    fun getSenderData(appId: String): Sender? {
        val call = apiService.getSenderId(appId)

        try {
            val response = call.execute()
            return response.body()
            //Log.d(TAG, "getSenderData: response = $response")
        } catch (e: IOException) {
            e.printStackTrace()
            //Log.d(TAG, "getSenderData: IOException = $e")
        }
        return null
    }

    fun createPush(pushUser: PushUser): String? {
        val call = apiService.createPush(pushUser)
        val response = call.enqueue(
            object : Callback<PushUser> {
                override fun onResponse(
                    call: Call<PushUser>, response: Response<PushUser>) {

                    if (response.code() == 500) {
                        Log.d(TAG, "createPush.onResponse: ERROR = 500")
                    }
                    //Log.d(TAG, "createPush.onResponse: response = $response")
                }

                override fun onFailure(call: Call<PushUser>, t: Throwable) {
                    //Log.d(TAG, "createPush.onFailure: Throwable = $t")
                }

            })
        return null
    }

    fun createSession(registrationId: String): Internal? {
        val call = apiService.createSession(registrationId)

        call.enqueue(
            object : Callback<Internal> {
                override fun onResponse(call: Call<Internal>, response: Response<Internal>) {
                    //Log.d(TAG, "createSession.onResponse: response = $response")
                }

                override fun onFailure(call: Call<Internal>, t: Throwable) {
                    //Log.d(TAG, "createSession.onFailure: t = $t")
                }
            }
        )
        return null
    }

    fun updateRegistrationId(pushInstance: PushInstance): String? {
        val call = apiService.updateUser(pushInstance)
        val response = call.enqueue(
            object : Callback<PushInstance> {
                override fun onResponse(
                    call: Call<PushInstance>,
                    response: Response<PushInstance>
                ) {
                    //Log.d(TAG, "updateRegistrationId.onResponse: response = $response")
                }

                override fun onFailure(call: Call<PushInstance>, t: Throwable) {
                    //Log.d(TAG, "updateRegistrationId.onFailure: t = $t")
                }

            }
        )
        return null
    }

    fun saveTag(tag: Tag): String? {
        val call = apiService.saveCustomParams(tag)
        Log.d(TAG, "saveCustomTag: tag = $tag")
        val response = call.enqueue(
           object : Callback<Tag> {
               override fun onResponse(call: Call<Tag>, response: Response<Tag>) {
                   //Log.d(TAG, "saveCustomTag.onResponse: response = $response")
               }

               override fun onFailure(call: Call<Tag>, t: Throwable) {
                   //Log.d(TAG, "saveCustomTag.onFailure: Throwable = $t")
               }
           }
        )
        return null
    }

    fun sendTimeStatistic(timeData: TimeData): String? {
        val call = apiService.sendDuration(timeData)
        //Log.d(TAG, "sendTimeStatistic: timeData = $timeData")
        call.enqueue(
            object : Callback<TimeData> {
                override fun onResponse(call: Call<TimeData>, response: Response<TimeData>) {
                    //Log.d(TAG, "sendTimeStatistic.onResponse: response = $response")
                }

                override fun onFailure(call: Call<TimeData>, t: Throwable) {
                    //Log.d(TAG, "sendTimeStatistic.onFailure: Throwable = $t")
                }
            }
        )
        return null
    }

    fun createTransition(registrationId: String): Internal? {
        val call = apiService.createTransition(registrationId)
        //Log.d(TAG, "createTransition: registrationId = $registrationId")
        call.enqueue(
            object : Callback<Internal> {
                override fun onResponse(call: Call<Internal>, response: Response<Internal>) {
                }

                override fun onFailure(call: Call<Internal>, t: Throwable) {
                    //Log.d(TAG, "createTransition.onFailure: Throwable = $t")
                }
            }
        )
        return null
    }
}