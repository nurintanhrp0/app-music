package com.example.musicapps.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.musicapps.GlobalApp
import com.example.musicapps.R
import com.example.musicapps.adapter.TrackAdapter
import com.example.musicapps.connections.networks.ConverterUrl
import com.example.musicapps.connections.networks.PathUrl
import com.example.musicapps.connections.response.GetListResponse
import com.example.musicapps.database.DaoSession
import com.example.musicapps.database.TTrackDao
import com.example.musicapps.model.Tracks
import com.example.musicapps.util.GlobalVariable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibraryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recycler: RecyclerView
    private lateinit var regionWaiting: ConstraintLayout
    private lateinit var regionNoResults: ConstraintLayout
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: TrackAdapter
    private lateinit var tryAgain: TextView

    private lateinit var trackLists: MutableList<Tracks>
    private lateinit var daoSession: DaoSession

    private var page = 1
    private var isLastPage = 0
    private val pageSize =10
    private lateinit var qArtist: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        daoSession = (activity?.applicationContext as GlobalApp).daoSession!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.list_item)
        regionWaiting = view.findViewById(R.id.regionWaiting)
        regionNoResults = view.findViewById(R.id.regionNoResults)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        tryAgain = view.findViewById(R.id.txtTryAgain)

        adapter = TrackAdapter(R.layout.music_list_row, daoSession)
        recycler.layoutManager = LinearLayoutManager(context)

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = (recycler.layoutManager as LinearLayoutManager)
                val lastItemVisible = linearLayoutManager.findLastVisibleItemPosition()
                val currentTotalCount = linearLayoutManager.itemCount
                if (currentTotalCount - 2 <= lastItemVisible) {
                    if (isLastPage == 0) {
                        page++
                        getTrackList()
                    }
                    return
                }
            }
        })

        swipeRefresh.setOnRefreshListener {
            page  = 1
            isLastPage = 0
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
        regionNoResults.visibility = View.GONE

        trackLists = ArrayList()
        isLastPage = 0
        getTrackList()
    }

    private fun getTrackList(){
        val apiService = ConverterUrl.client!!.create(PathUrl::class.java)
        val call = apiService.getTrackList(GlobalVariable.CONFIG_API_KEY, page, pageSize, qArtist)
        call.enqueue(object : Callback<GetListResponse> {
            override fun onResponse(call: Call<GetListResponse>, response: Response<GetListResponse>) {
               regionWaiting.visibility = View.GONE

                if (!response.isSuccessful) {
                    Toast.makeText(context, "${getString(R.string.label_error_2)}${response.code()}", Toast.LENGTH_SHORT).show()
                    return
                }

                if (response.isSuccessful) {
                    if (response.body().message == null){
                        Toast.makeText(context, getString(R.string.label_error_1), Toast.LENGTH_SHORT).show()
                        return
                    }

                    val messages = response.body().message
                    if (messages!!.header == null){
                        Toast.makeText(context, getString(R.string.label_error_1), Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (messages.header!!.statusCode != 200){
                        Toast.makeText(context, "${getString(R.string.label_error_2)}${messages.header!!.statusCode}", Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (messages.body != null) {
                        try {

                            val tracksList = messages.body!!.trackList
                            if (trackLists.isNotEmpty() && page != 1){
                                trackLists.removeLast()
                            }else{
                                trackLists = ArrayList()
                            }

                            if (tracksList != null){
                                regionNoResults.visibility = View.GONE
                                if (tracksList.size >0) {
                                    var track: Tracks
                                    for (models in tracksList) {
                                        val model = models.trackList
                                        track = Tracks()
                                        val isFavorite = daoSession.tTrackDao.queryBuilder().where(TTrackDao.Properties.TrackId.eq(model!!.trackId)).list().size > 0
                                        track.trackId = model.trackId
                                        track.trackName = model.trackName
                                        track.artistName = model.artistName
                                        track.isFavorite = if (isFavorite){1}else{0}
                                        track.isLoading = 0
                                        trackLists.add(track)
                                    }

                                    //add loading card
                                    track = Tracks()
                                    track.trackId = 0
                                    track.trackName = ""
                                    track.artistName = ""
                                    track.isFavorite = 0
                                    track.isLoading = 1
                                    trackLists.add(track)

                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    adapter.setMainData(trackLists)
                    if (page == 1) {
                        recycler.adapter = adapter
                        if (trackLists.isEmpty()) {
                            regionNoResults.visibility = View.VISIBLE
                        }
                    }

                    if (trackLists.isNotEmpty() && messages.header!!.available <= trackLists.size){
                        isLastPage = 1
                        trackLists.removeLast()
                        Toast.makeText(context, getString(R.string.label_limit_page), Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }

            override fun onFailure(call: Call<GetListResponse>, t: Throwable) {
                try {
                    regionWaiting.visibility = View.GONE

                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    override fun onStart() {
        super.onStart()
        reloadList()
    }

    override fun onResume() {
        super.onResume()
        reloadList()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LibraryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LibraryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}