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
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.ReasonList
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_report_reason.view.*

class PostReportListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val type: String,
    val data: List<ReasonList.Data>


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
                .inflate(R.layout.item_report_reason, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder
           // getScrennwidth()
            holder1.bind(holder.adapterPosition, onItemClick,widthNew,heightNew,data[position])


        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    /*override fun getItemViewType(position: Int): Int {
        return if (orderdetails[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }*/

    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            position: Int,
            onitemClick: OnItemClick,
            widthNew: Int,
            heightNew: Int,
            data: ReasonList.Data

        ) =
            with(itemView) {
                tv_post_reason_name.text = data.reasonName
                ll_reason_report.setOnClickListener {
                    onitemClick.onClicklisneter(position,"reason",ll_reason_report,data.reasonID)
                }

            }

    }


    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String, v: View,reasonid:String)

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