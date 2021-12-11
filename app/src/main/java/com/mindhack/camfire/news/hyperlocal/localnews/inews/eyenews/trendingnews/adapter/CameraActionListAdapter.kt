package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import kotlinx.android.synthetic.main.item_single_small_text.view.*

class CameraActionListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val arraydata: ArrayList<String>

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_single_small_text, parent, false)
        return ViewHolder(v, context)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder1 = holder as CameraActionListAdapter.ViewHolder
        holder1.bind(holder1.adapterPosition, onItemClick, arraydata, mSelection)
    }

    override fun getItemCount(): Int {
        return arraydata.size
    }

    /*override fun getItemViewType(position: Int): Int {
        return if (orderdetails[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }*/

    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            position: Int,
            onitemClick: OnItemClick,
            arraydata: ArrayList<String>,
            mSelection: Int
        ) =
            with(itemView) {
                itemView.textSmall.text = arraydata[position]

                if (mSelection == position) {
                    itemView.textSmall.setTextColor(resources.getColor(R.color.white))
                    itemView.ivSelected.visibility = View.VISIBLE

                } else {
                    itemView.textSmall.setTextColor(resources.getColor(R.color.white))
                    itemView.ivSelected.visibility = View.GONE
                }

                itemView.setOnClickListener {
                    onitemClick.onClicklisneter(position, arraydata[position])
                }
            }
    }


    interface OnItemClick {
        fun onClicklisneter(
            pos: Int,
            name: String
        )

    }
}