package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.ReasonList
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.material.internal.BaselineLayout

import java.util.*

class ReasonAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val languagelist: ArrayList<ReasonList.Data?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var sessionManager: SessionManager = SessionManager(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_language_setting, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            holder.tvLanguage.text = languagelist[position]!!.reasonName

            holder.radioLanguage.visibility = View.VISIBLE
            holder.tvLanguage2.visibility = View.GONE

            if (languagelist[position]!!.isSelected){
                holder.radioLanguage.setImageResource(R.drawable.camfire_after_search)
            }else{
                holder.radioLanguage.setImageResource(R.drawable.camfire_after)
            }

            if (position == languagelist.size - 1){
                holder.baseLanguage.visibility = View.GONE
            }else holder.baseLanguage.visibility = View.VISIBLE

            holder.ll_item_language.setOnClickListener {
                if(holder.adapterPosition>-1) {
                    if (mSelection != holder.adapterPosition) {

                        mSelection = holder.adapterPosition
                        if (onItemClick != null)
                            onItemClick.onClicklisneter(holder.adapterPosition, languagelist[holder.adapterPosition]!!.reasonID!!)
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return languagelist.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (languagelist[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        val tvLanguage = itemView.findViewById(R.id.tvLanguage) as TextView
        val tvLanguage2 = itemView.findViewById(R.id.tvLanguage2) as TextView
        val radioLanguage = itemView.findViewById(R.id.radioLanguage) as AppCompatImageView
        val ll_item_language = itemView.findViewById(R.id.ll_item_language) as LinearLayout
        val baseLanguage = itemView.findViewById(R.id.baseLanguage) as BaselineLayout


    }


    interface OnItemClick {
        fun onClicklisneter(pos: Int, reasonID: String)

    }
}