package com.example.musicapps.connections.response

import com.example.musicapps.model.Message
import com.google.gson.annotations.SerializedName

class GetListResponse {

    @SerializedName("message")
    var message: Message? = null
}