package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.application

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.facebook.drawee.backends.pipeline.Fresco
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AWSConfiguration
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.BuildConfig
import java.io.File


class MyApplication :  Application() {

    companion object {
        @kotlin.jvm.JvmField
        var simpleCache: SimpleCache? = null

        lateinit var instance: MyApplication
            private set
        var credentials: BasicAWSCredentials? = null
        var mContext: Context? = null

        var credentialsProvider :CognitoCachingCredentialsProvider?=null
        protected var userAgent: String? = null

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MultiDex.install(this)
        Fresco.initialize(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        mContext = this@MyApplication

        userAgent = Util.getUserAgent(this, "ExoPlayerDemo")

        val evictor = LeastRecentlyUsedCacheEvictor(1024 * 1024 * 10)
        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(mContext)
        val cacheFolder = File(mContext?.cacheDir, "exoCache")
        createChannel()
        simpleCache =SimpleCache(cacheFolder, evictor, databaseProvider)

        GetDynamicStringDictionaryObjectClass.getInstance(this)

        credentialsProvider= CognitoCachingCredentialsProvider(
            mContext,
            mContext?.getString(R.string.pool_id),  // Identity pool ID
            Regions.AP_SOUTH_1 // Region
        )
//        var appSignature = AppSignatureHelper(this)
//        appSignature.appSignatures
    }



    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channelId = MyUtils.channelId
            // Create the NotificationChannel
            val name = instance.getString(R.string.app_name)
            val descriptionText = "Booking Request Customer"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val existingChannel = notificationManager.getNotificationChannel(channelId)

//it will delete existing channel if it exists
            if (existingChannel != null) {
                notificationManager.deleteNotificationChannel(existingChannel.id)
            }


            /*var soundUri =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + instance.packageName + "/" + R.raw.booking_ring)*/
            var defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            var mChannel = NotificationChannel(channelId, name, importance)



            mChannel.description = descriptionText
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            mChannel.enableVibration(true)

            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)


            notificationManager.createNotificationChannel(mChannel)
        }
    }

    /*fun useExtensionRenderers(): Boolean {
        return "withExtensions" == BuildConfig.FLAVOR
    }*/

    fun buildDataSourceFactory(): DataSource.Factory? {
        val upstreamFactory =
            DefaultDataSourceFactory(this, buildHttpDataSourceFactory())
        return buildReadOnlyCacheDataSource(
            upstreamFactory,
           simpleCache as Cache
        )
    }


    /** Returns a [HttpDataSource.Factory].  */
    fun buildHttpDataSourceFactory(): HttpDataSource.Factory? {
        return DefaultHttpDataSourceFactory(userAgent)
    }

    private fun buildReadOnlyCacheDataSource(
        upstreamFactory: DefaultDataSourceFactory,
        cache: Cache
    ): CacheDataSourceFactory? {
        return CacheDataSourceFactory(
            cache,
            upstreamFactory,
            FileDataSourceFactory(),  /* cacheWriteDataSinkFactory= */
            null,
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,  /* eventListener= */
            null
        )
    }

}