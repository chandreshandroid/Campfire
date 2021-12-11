package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Place
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_suggested_location.view.*

class GlobalSearchLocationListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val type: String,
    val suggestedLocationList: ArrayList<Place>

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var widthNew = 0
    var heightNew = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_global_search_location, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder

            holder1.bind(holder.adapterPosition, suggestedLocationList.get(position),onItemClick)


        }
    }

    override fun getItemCount(): Int {
        return if (suggestedLocationList.size < 5) suggestedLocationList.size else 5
    }

    /*override fun getItemViewType(position: Int): Int {
        return if (orderdetails[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }*/

    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            position: Int,
            suggestedLocation: Place,
            onItemClick: OnItemClick
        ) =
            with(itemView) {
                tv_suggestedLocation.text=suggestedLocation.postLocation
                itemView.setOnClickListener {

                    onItemClick.onClicklisneter(position, suggestedLocation.postLocation)
                }
            }

    }


    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String)

    }
    private fun getScrennwidth(): Int {

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        widthNew = (width / 3).toInt()
        heightNew = (height / 4).toInt()

        return height
    }
}