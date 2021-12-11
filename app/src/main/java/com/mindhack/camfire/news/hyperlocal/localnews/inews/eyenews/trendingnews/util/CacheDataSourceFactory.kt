package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import android.content.Context
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.application.MyApplication
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util

/**
 * Created by dhavalkaka on 15/03/2018.
 */
class CacheDataSourceFactory1(
    private val context: Context,
    private val maxFileSize: Long
) : DataSource.Factory {

    private val defaultDatasourceFactory: DefaultDataSourceFactory

    override fun createDataSource(): DataSource {
        return CacheDataSource(
            MyApplication.simpleCache,
            defaultDatasourceFactory.createDataSource(),
            FileDataSource(),

            CacheDataSink(MyApplication.simpleCache, maxFileSize),
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
            null
        )
    }

    init {
        val userAgent = Util.getUserAgent(
            context,
            context.getString(R.string.app_name)
        )
        val bandwidthMeter = DefaultBandwidthMeter()
        defaultDatasourceFactory = DefaultDataSourceFactory(
            context,
            bandwidthMeter,
            DefaultHttpDataSourceFactory(userAgent, bandwidthMeter)
        )
    }
}