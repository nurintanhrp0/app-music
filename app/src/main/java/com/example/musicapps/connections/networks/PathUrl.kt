package com.example.musicapps.connections.networks

import com.example.musicapps.connections.response.GetListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PathUrl {

    @GET("ws/1.1/track.search")
    fun getTrackList(
        @Query("apikey") apiKey: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("q_artist") qArtist: String,
    ):  Call<GetListResponse>

}
