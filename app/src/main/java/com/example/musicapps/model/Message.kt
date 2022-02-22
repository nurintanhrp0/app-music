package com.example.musicapps.model

import com.google.gson.annotations.SerializedName

class Message {
    @SerializedName("header")
    var header: Header? = null

    @SerializedName("body")
    var body: Body? = null

}
