package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.notification

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.BADGE_ICON_SMALL
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.AsyncTask
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.SplashActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Push
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


import com.google.gson.Gson


/**
 * Created by Chandresh
 * FIL
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var push = Push()
    var sessionManager: SessionManager? = null

    @SuppressLint("WrongThread")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            if (remoteMessage?.data != null) {
                val message = Gson().toJson(remoteMessage.data)
                val push: Push?
                Log.d(TAG, remoteMessage.data.toString())
                push = Gson().fromJson(message, Push::class.java)

                if (push != null) {
                    if (ForegroundCheckTask().execute(this).get())
                    {
                            val intentBroadcast = Intent(Push.action)
                            intentBroadcast.putExtra("PushData", push)
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast)
                        if (push.type.equals("Logout", true)) {
                            sessionManager!!.clear_login_session()
                        }
                        else{

                            sendNotification(push,this)
                        }
                    } else {
                        if (push.type.equals("Logout", true)) {
                            sessionManager!!.clear_login_session()
                        }
                        else{

                             sendNotification(push,this)
                        }
                    }



                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    fun sendNotification(push: Push, context: Context) {

        try {


            sessionManager = SessionManager(context)
            sessionManager!!.NotificationRead = false
            val msg = push.msg

//            PrefDb(context).putInt("unReadNotification", 1)
            var intent: Intent? = null


            intent = Intent(context, SplashActivity::class.java)
            intent.putExtra("Push", push)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
            var soundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            /*if(    push.type.equals(Push.bokingRequest, true) )

            soundUri=Uri.parse("android.resource://" + context.packageName + "/" + R.raw.booking_ring)*/

            val notificationCompat = NotificationCompat.Builder(context, context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.logo_for_notification_bar)
                .setLargeIcon(largeIcon)
                .setBadgeIconType(BADGE_ICON_SMALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setContentTitle(context.getString(R.string.app_name))

                .setSound(soundUri)

                .setPriority(NotificationCompat.PRIORITY_MAX)

            val notification = notificationCompat.build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                val id = context.getString(R.string.app_name)
                val name = context.getString(R.string.app_name)
                val importance = NotificationManager.IMPORTANCE_HIGH
                var channel: NotificationChannel? = null
                channel = NotificationChannel(id, name, importance)
                channel.description = msg
                channel.setShowBadge(true)


                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                channel.setSound(soundUri, audioAttributes)
                notificationManager.createNotificationChannel(channel)
            }


            val requestID = System.currentTimeMillis().toInt()
           /* if(push.type.equals(Push.bokingRequest))
            {


               MyUtils. playSoundVibrate(context,true)
            }*/

            notificationManager.notify(requestID, notification)


            val intentBroadcast = Intent("Push")
            intentBroadcast.putExtra("pushdata", push)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentBroadcast)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.name


    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, p0.toString())
    }

    internal inner class ForegroundCheckTask : AsyncTask<Context, Void, Boolean>() {

        override fun doInBackground(vararg params: Context): Boolean? {
            val context = params[0].applicationContext
            return isAppOnForeground(context)
        }

        private fun isAppOnForeground(context: Context): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager.runningAppProcesses ?: return false
            val packageName = context.packageName
            for (appProcess in appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                    return true
                }
            }
            return false
        }
    }
}