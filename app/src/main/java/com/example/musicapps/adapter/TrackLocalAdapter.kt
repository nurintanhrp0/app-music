package com.example.musicapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.musicapps.R
import com.example.musicapps.database.DaoSession
import com.example.musicapps.database.TTrack
import com.example.musicapps.database.TTrackDao

class TrackLocalAdapter(private val rowLayout: Int, private var models: MutableList<TTrack>, private val daoSession: DaoSession)
    : androidx.recyclerview.widget.RecyclerView.Adapter<TrackLocalAdapter.ViewHolder>() {

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        var txtSinger: TextView = v.findViewById(R.id.txtSinger)
        var txtTitle: TextView = v.findViewById(R.id.txtTitle)
        var btnAddFavorite: ImageView = v.findViewById(R.id.btnAddFavorite)
        var btnRemoveFavorite: ImageView = v.findViewById(R.id.btnRemoveFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        holder.txtSinger.text = model.artisName
        holder.txtTitle.text = model.trackName
        holder.btnAddFavorite.visibility = View.GONE
        holder.btnRemoveFavorite.visibility = View.VISIBLE

        holder.btnRemoveFavorite.setOnClickListener {
           val data = daoSession.tTrackDao.queryBuilder().where(TTrackDao.Properties.TrackId.eq(model.trackId)).unique()
            daoSession.tTrackDao.delete(data)

            models.removeAt(position)
            notifyItemRemoved(position)
            notifyItemChanged(position, models.size)

        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
