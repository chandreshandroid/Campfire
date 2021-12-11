package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Tag
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.item_global_search.view.*

import java.util.*

class GlobalSearchAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val languagelist: ArrayList<Tag>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var sessionManager: SessionManager = SessionManager(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_global_search, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder

            holder1.bind(holder.adapterPosition, languagelist.get(position),onItemClick,languagelist.size)

        }
    }

    override fun getItemCount(): Int {
        return languagelist.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (languagelist[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            position: Int,
            suggestedLocation: Tag,
            onItemClick: OnItemClick,
            size: Int
        ) =
            with(itemView) {

                imv_user_dp.setImageURI(suggestedLocation.postSerializedData[0].albummedia[0].albummediaFile)
                tvFeedUserName.text=suggestedLocation.posthashText
                tvFeedUserId.text=suggestedLocation.totalPost +" Posts"

                itemView.setOnClickListener {

                    onItemClick.onClicklisneter(position, suggestedLocation.posthashText)
                }
            }


    }


    interface OnItemClick {
        fun onClicklisneter(pos: Int, reasonID: String)

    }
}