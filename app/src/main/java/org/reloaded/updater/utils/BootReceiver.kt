package org.reloaded.updater.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import androidx.core.app.NotificationCompat
import org.reloaded.updater.MainActivity
import org.reloaded.updater.api.Response
import org.reloaded.updater.tasks.CheckUpdate

class BootReceiver: BroadcastReceiver() {

    private val TAG="ReloadedOTA"

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            Thread.sleep(30000) // Give the system some time to warm up
            Log.i(TAG, "Checking for updates on boot...")
            if (Common.checkNetwork(context!!)) {
                val checkUpdateTask = CheckUpdate(true, object : CheckUpdate.UpdateCheckerCallback {

                    override val callbackContext: Context
                        get() = context

                    override fun processResult(response: Response) {
                        if (response.deviceSupported == 1 && response.updateAvailable == 1) {
                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val notifyID = 1
                            val id = "reloaded_update"
                            val name = context.getString(org.reloaded.updater.R.string.channel)
                            val description = context.getString(org.reloaded.updater.R.string.channel_description)
                            val importance = NotificationManager.IMPORTANCE_LOW
                            val mChannel = NotificationChannel(id, name, importance)
                            mChannel.description = description
                            notificationManager.createNotificationChannel(mChannel)

                            val mBuilder = NotificationCompat.Builder(context.applicationContext, "Reloaded Updates")
                                .setSmallIcon(org.reloaded.updater.R.drawable.ic_device)
                                .setContentTitle(context.getString(org.reloaded.updater.R.string.notification_title))
                                .setContentText(context.getString(org.reloaded.updater.R.string.notification_message))
                                .setOnlyAlertOnce(true)
                                .setAutoCancel(true)
                                .setChannelId(id)

                            val mIntent = Intent(context, MainActivity::class.java)
                            mIntent.action = Intent.ACTION_MAIN
                            mIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            mIntent.putExtra("response", response)
                            val pendingIntent = PendingIntent.getActivity(
                                context, 0, mIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                            mBuilder.setContentIntent(pendingIntent)
                            notificationManager.notify(notifyID, mBuilder.build())
                        }

                    }
                })
                checkUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}