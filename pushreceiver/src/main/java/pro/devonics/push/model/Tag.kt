package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class Tag(

    private val key: String,
    private val value: String,

    @SerializedName("registration_id")
    private val registrationId: String
)
