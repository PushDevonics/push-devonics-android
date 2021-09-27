package pro.devonics.push.push_receiver.model

import com.google.gson.annotations.SerializedName

data class TimeData(
    @SerializedName("registration_id")
    private val registrationId: String,
    private val time: Long
)
