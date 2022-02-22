package com.example.musicapps.model

import com.google.gson.annotations.SerializedName

class Tracks {
    @SerializedName("track_id")
    var trackId: Long = 0L

    @SerializedName("track_name")
    var trackName: String = ""

    @SerializedName("artist_name")
    var artistName: String = ""

    @SerializedName("is_favorite")
    var isFavorite: Int = 0

    @SerializedName("is_loading")
    var isLoading: Int = 0
}
