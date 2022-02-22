package com.example.musicapps.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.musicapps.GlobalApp
import com.example.musicapps.R
import com.example.musicapps.adapter.TrackLocalAdapter
import com.example.musicapps.database.DaoSession
import com.example.musicapps.model.Tracks

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recycler: RecyclerView
    private lateinit var regionWaiting: ConstraintLayout
    private lateinit var regionNoResultsFavorite: ConstraintLayout
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: TrackLocalAdapter
    private lateinit var tryAgain: TextView

    private lateinit var trackLists: MutableList<Tracks>
    private lateinit var daoSession: DaoSession

    private lateinit var qArtist: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        daoSession = (activity?.applicationContext as GlobalApp).daoSession!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.list_item_favorite)
        regionWaiting = view.findViewById(R.id.regionWaitingFavorite)
        regionNoResultsFavorite = view.findViewById(R.id.regionNoResultsFavorite)
        swipeRefresh = view.findViewById(R.id.swipeRefreshFavorite)
        tryAgain = view.findViewById(R.id.txtTryAgain)

        swipeRefresh.setOnRefreshListener {
            reloadList()
            Handler().postDelayed({
                swipeRefresh.isRefreshing = false
            }, 1000)
        }

        tryAgain.setOnClickListener { reloadList() }

        reloadList()

    }

    private fun reloadList(){
        val bundle = this.arguments
        qArtist = bundle!!.getString("q_artist")!!
        regionWaiting.visibility = View.VISIBLE
        regionNoResultsFavorite.visibility = View.GONE
        trackLists = ArrayList()

        getTrackList()
    }

    private fun getTrackList(){
        var trackList = daoSession.tTrackDao.loadAll()
        if (qArtist != "") {
            if (trackList.isNotEmpty()) {
                val list = trackList.filter {
                    it.artisName.toLowerCase().contains(qArtist.toLowerCase())
                }

                trackList = list.toMutableList()
            }
        }

        adapter = TrackLocalAdapter(R.layout.music_list_row, trackList, daoSession)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        adapter.notifyDataSetChanged()

        if (trackList.isEmpty()){
            regionNoResultsFavorite.visibility = View.VISIBLE
        }else{
            regionNoResultsFavorite.visibility = View.GONE
        }

        regionWaiting.visibility = View.GONE

    }



    override fun onStart() {
        super.onStart()
        reloadList()
    }

    override fun onResume() {
        super.onResume()
        reloadList()
    }
}