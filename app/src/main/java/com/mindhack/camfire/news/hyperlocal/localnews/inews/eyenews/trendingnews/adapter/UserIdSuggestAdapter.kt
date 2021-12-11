package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

import java.util.ArrayList

class UserIdSuggestAdapter(@NonNull context: Context, resource: Int) :
    ArrayAdapter<String>(context, resource), Filterable {
    private val mlistData: MutableList<String>

    init {
        mlistData = ArrayList()
    }

    fun setData(list: List<String>) {
        mlistData.clear()
        mlistData.addAll(list)
    }

    override fun getCount(): Int {
        return mlistData.size
    }

    @Nullable
    override fun getItem(position: Int): String? {
        return mlistData[position]
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    fun getObject(position: Int): String {
        return mlistData[position]
    }

    @NonNull
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    filterResults.values = mlistData
                    filterResults.count = mlistData.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}