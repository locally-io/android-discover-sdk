package io.locally.discover.authentication.model

import com.google.gson.annotations.SerializedName

class LoginResponse(val data: Data) {
    class Data(@SerializedName("identity_pool_id") val identityId: String,
               @SerializedName("token") val token: String,
               @SerializedName("publisher_id") val publisherId: String)
}