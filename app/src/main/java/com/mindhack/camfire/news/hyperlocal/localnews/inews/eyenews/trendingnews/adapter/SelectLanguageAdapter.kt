package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetLanguageListPojo
import kotlinx.android.synthetic.main.item_select_language.view.*

class SelectLanguageAdapter(
    var context: Context,
    var arrayData: ArrayList<GetLanguageListPojo.Datum?>,
    val btnlistener: BtnClickListener
) : RecyclerView.Adapter<SelectLanguageAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectLanguageAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_select_language, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: SelectLanguageAdapter.ViewHolder, position: Int) {

        holder.itemSelectLanguageTextLanguage.text = arrayData[position]?.languageName

        if (arrayData[position]?.isSelected!!){
            holder.radioLanguage.setBackgroundDrawable(context.getDrawable(R.drawable.custom_thumb))
        }else{
            holder.radioLanguage.setBackgroundDrawable(context.getDrawable(R.drawable.custom_thumb2))
        }

        holder.itemView.setOnClickListener {
            if (btnlistener != null){
                btnlistener.onBtnClick(holder?.adapterPosition, arrayData[position]?.languageName!!)
            }
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return arrayData.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemSelectLanguageTextLanguage = itemView.itemSelectLanguageTextLanguage
        var radioLanguage = itemView.radioLanguage
    }

    open interface BtnClickListener {
        fun onBtnClick(position: Int, buttonName: String)
    }
}