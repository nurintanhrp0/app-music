package com.example.musicapps.model

import com.google.gson.annotations.SerializedName

class Body {
    @SerializedName("track_list")
    var trackList: MutableList<TrackList>? = null
}
