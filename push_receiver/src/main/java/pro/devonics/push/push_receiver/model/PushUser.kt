package pro.devonics.push.push_receiver.model

import com.google.gson.annotations.SerializedName

data class PushUser(

    @SerializedName("registration_id")
    private val registrationId: String,

    @SerializedName("app_id")
    private val appId: String,

    @SerializedName("platform_type")
    private val platformType: String = "Android",

    private val country: String,
    private val language: String,

    @SerializedName("timezone")
    private val timeZone: String,

    @SerializedName("device_model")
    private val deviceModel: String,
) {
    fun getRegistrationId(): String {
        return registrationId
    }
}