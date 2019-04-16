package org.reloaded.updater.utils

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.AsyncTask
import org.reloaded.updater.api.Response
import org.reloaded.updater.tasks.CheckUpdate
import java.lang.Exception
import android.app.PendingIntent
import android.content.Intent
import org.reloaded.updater.MainActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import org.reloaded.updater.R

class OTAService: JobService(), CheckUpdate.UpdateCheckerCallback {

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        try {
            if (Common.checkNetwork(this)) {
                val checkUpdateTask = CheckUpdate(true, this)
                checkUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return false
    }

    override val callbackContext: Context
        get() = this

    override fun processResult(response: Response) {

        if (response.deviceSupported == 1 && response.updateAvailable == 1) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notifyID = 1
            val id = "reloaded_update"
            val name = getString(R.string.channel)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description
            notificationManager.createNotificationChannel(mChannel)

            val mBuilder = NotificationCompat.Builder(applicationContext, "Reloaded Updates")
                .setSmallIcon(R.drawable.ic_device)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_message))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setChannelId(id)

            val intent = Intent(this, MainActivity::class.java)
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("response", response)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(pendingIntent)
            notificationManager.notify(notifyID, mBuilder.build())
        }

    }

}