package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData

/**
 * Created by dhavalkaka on 29/03/2018.
 */
interface NotifyInterface {
    fun notifyData(
        feedDatum: TrendingFeedData?,
        isDelete: Boolean,
        isDeleteComment: Boolean,
        postComment: String?
    )
}