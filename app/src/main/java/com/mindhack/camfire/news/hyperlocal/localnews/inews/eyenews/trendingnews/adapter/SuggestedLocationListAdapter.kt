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
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase.User
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_suggested_location.view.*

class SuggestedLocationListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val type: String,
    val suggestedLocationList: ArrayList<User>

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
                .inflate(R.layout.item_suggested_location, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder

            holder1.bind(holder.adapterPosition, suggestedLocationList.get(position).searchLocationName!!, suggestedLocationList.get(position).searchLocationLate!!, suggestedLocationList.get(position).searchLocationLong!!,onItemClick,widthNew,heightNew)


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
            suggestedLocation:String,
            suggestedLocationLate : Double,
            suggestedLocationLong : Double,
            onitemClick: OnItemClick,
            widthNew: Int,
            heightNew: Int
        ) =
            with(itemView) {
                tv_suggestedLocation.text = suggestedLocation
                itemView.setOnClickListener {
                    onitemClick.onClicklisneter(position, suggestedLocation, suggestedLocationLate, suggestedLocationLong, itemView)
                }
            }

    }


    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String, late : Double, long : Double, v: View)

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