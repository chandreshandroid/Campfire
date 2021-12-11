package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.clusterRenderer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.ImagesDetailsFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.VideoDetailsFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Person
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.ClusterListFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import java.io.Serializable

@SuppressLint("InflateParams")
class MarkerClusterRenderer(val context: Context,val googleMap: GoogleMap, clusterManager: ClusterManager<Person>) : DefaultClusterRenderer<Person>(context, googleMap, clusterManager),

    OnClusterClickListener<Person>, OnInfoWindowClickListener
{

     var layoutInflater: LayoutInflater?=null
     var clusterIconGenerator: IconGenerator?=null
     var clusterItemView: View?=null
     var feedList: ArrayList<TrendingFeedData?>? = ArrayList()

    override fun onBeforeClusterItemRendered(
        item: Person,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.news_post_spot_map_pin_small))
        markerOptions.title(item.title)
    }

    override fun onBeforeClusterRendered(
        cluster: Cluster<Person>,
        markerOptions: MarkerOptions
    ) {
        val singleClusterMarkerSizeTextView =
            clusterItemView?.findViewById<TextView>(R.id.singleClusterMarkerSizeTextView)
        singleClusterMarkerSizeTextView?.text = cluster.size.toString()
        val icon = clusterIconGenerator?.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun onClusterItemRendered(
        clusterItem: Person,
        marker: Marker
    ) {
        marker.tag = clusterItem
    }

    override fun onClusterClick(cluster: Cluster<Person>): Boolean {

        if (cluster == null)
            return false
        val builder = LatLngBounds.Builder()
        for (user in cluster.items){
            builder.include(user.position)
            feedList?.add(user.trendingFeedData)
        }

        val bounds = builder.build()
        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            var clusterListFragment = ClusterListFragment()
            Bundle().apply {
                putSerializable("feedDataCluster",feedList)
                clusterListFragment.arguments = this
            }
            (context as MainActivity).navigateTo(
                clusterListFragment,
                clusterListFragment::class.java.name,
                true
            )
            Log.e("ClusterSize",""+cluster.items.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun onInfoWindowClick(marker: Marker) {
        val context = clusterItemView?.context
        val user =
            marker.tag as Person? //  handle the clicked marker object

        if (context != null && user != null)
            Toast.makeText(
            context,
            user.name,
            Toast.LENGTH_SHORT
        ).show()
    }

    private inner class MyCustomClusterItemInfoView : InfoWindowAdapter {
        private var clusterItemView: View?=null

       constructor() {
           clusterItemView = layoutInflater!!.inflate(R.layout.marker_info_window, null)
        }
        override fun getInfoWindow(marker: Marker): View {

            val user = marker.tag as Person? ?: return clusterItemView!!

            val itemNameTextView =
                clusterItemView?.findViewById<TextView>(R.id.itemNameTextView)
            val itemAddressTextView =
                clusterItemView?.findViewById<TextView>(R.id.itemAddressTextView)

            itemNameTextView?.text = marker.title
            itemAddressTextView?.text = user.title
            googleMap.setOnInfoWindowClickListener(object : OnInfoWindowClickListener{
                override fun onInfoWindowClick(p0: Marker?) {
                    if (user.getTrendindData().postMediaType.equals("Video", false))
                    {
                        var videoDeailsFragment = VideoDetailsFragment()
                        Bundle().apply {
                            putSerializable("feedData", user.getTrendindData())
                            videoDeailsFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            videoDeailsFragment,
                            videoDeailsFragment::class.java.name,
                            true
                        )


                    }
                    else if (user.getTrendindData()?.postMediaType.equals("Photo", false)) {
                        var imagesDetailsFragment = ImagesDetailsFragment()
                        Bundle().apply {
                            putSerializable("feedData",user.getTrendindData())
                            imagesDetailsFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            imagesDetailsFragment,
                            imagesDetailsFragment::class.java.name,
                            true
                        )


                    }
                }

            })
            return clusterItemView!!
        }

        override fun getInfoContents(marker: Marker): View {
            return null!!

            clusterItemView = layoutInflater?.inflate(R.layout.marker_info_window, null)!!

        }
    }

    init {
        layoutInflater = LayoutInflater.from(context)
        clusterItemView =
            layoutInflater?.inflate(R.layout.single_cluster_marker_view, null)!!
        clusterIconGenerator = IconGenerator(context)
        val drawable =
            ContextCompat.getDrawable(context, android.R.color.transparent)
        clusterIconGenerator?.setBackground(drawable)
        clusterIconGenerator?.setContentView(clusterItemView)
        clusterManager.setOnClusterClickListener(this)

        googleMap.setInfoWindowAdapter(clusterManager.markerManager)
        googleMap.setOnInfoWindowClickListener(this)
        clusterManager.markerCollection.setOnInfoWindowAdapter(MyCustomClusterItemInfoView())

        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
    }
}