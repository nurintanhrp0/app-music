package com.example.musicapps.model

import com.google.gson.annotations.SerializedName

class Header {
    @SerializedName("status_code")
    var statusCode: Int = 0

    @SerializedName("execute_time")
    var executeTime: Double = 0.0

    @SerializedName("available")
    var available: Long = 0L
}
