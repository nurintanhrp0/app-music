package com.example.musicapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.musicapps.R
import com.example.musicapps.database.DaoSession
import com.example.musicapps.database.TTrack
import com.example.musicapps.model.Tracks

class TrackAdapter(private val rowLayout: Int, private val daoSession: DaoSession)
    : androidx.recyclerview.widget.RecyclerView.Adapter<TrackAdapter.ViewHolder>() {

    private var mainData: List<Tracks>? = null

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        var txtSinger: TextView = v.findViewById(R.id.txtSinger)
        var txtTitle: TextView = v.findViewById(R.id.txtTitle)
        var btnAddFavorite: ImageView = v.findViewById(R.id.btnAddFavorite)
        var btnRemoveFavorite: ImageView = v.findViewById(R.id.btnRemoveFavorite)
        var progressbar: ProgressBar = v.findViewById(R.id.progressbar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mainData!![position]
        holder.txtSinger.text = model.artistName
        holder.txtTitle.text = model.trackName

        if (model.isLoading == 0){
            holder.txtSinger.visibility = View.VISIBLE
            holder.txtTitle.visibility = View.VISIBLE
            holder.progressbar.visibility = View.INVISIBLE

            if (model.isFavorite == 0){
                holder.btnAddFavorite.visibility = View.VISIBLE
            }else{
                holder.btnAddFavorite.visibility = View.GONE
            }
        }else{
            holder.progressbar.visibility = View.VISIBLE
            holder.txtSinger.visibility = View.INVISIBLE
            holder.txtTitle.visibility = View.INVISIBLE
            holder.btnAddFavorite.visibility = View.GONE
            holder.btnRemoveFavorite.visibility = View.GONE
        }

        holder.btnAddFavorite.setOnClickListener {
            model.isFavorite = 1
            holder.btnAddFavorite.visibility = View.GONE

            val tracks = TTrack()
            tracks.trackId = model.trackId
            tracks.trackName = model.trackName
            tracks.artisName = model.artistName
            tracks.isFavorite = model.isFavorite
            tracks.isLoading = model.isLoading
            daoSession.tTrackDao.insert(tracks)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return mainData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setMainData(mainData: List<Tracks>?) {
        this.mainData = mainData
        notifyDataSetChanged()
    }
}
