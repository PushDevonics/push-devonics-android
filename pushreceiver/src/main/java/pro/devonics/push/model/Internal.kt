package pro.devonics.push.model

import com.google.gson.annotations.SerializedName

data class Internal(
    val status: String,
    @SerializedName("internal_id")
    val internalId: String
)
