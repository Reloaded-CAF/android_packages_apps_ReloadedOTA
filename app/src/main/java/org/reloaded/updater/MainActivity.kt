@file:Suppress("ConstantConditionIf")

package org.reloaded.updater

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.alespero.expandablecardview.ExpandableCardView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.rominfo_layout.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import org.reloaded.updater.api.Response
import org.reloaded.updater.tasks.CheckUpdate
import org.reloaded.updater.utils.BootReceiver
import org.reloaded.updater.utils.Common
import org.reloaded.updater.utils.OTAService

class MainActivity : AppCompatActivity(), CheckUpdate.UpdateCheckerCallback {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /* R.id.card is the reference for the root CardView
         * in the ExpandableCardView.By referencing this root
         * CardView we're changing the background color of ExpandableCardVIew
         */
        latest_build.findViewById<ExpandableCardView>(R.id.card).backgroundColor =
            resources.getColor(R.color.cardBackground)
        latest_build.findViewById<TextView>(R.id.title).textColor = Color.WHITE
        latest_build.findViewById<ImageView>(R.id.arrow).imageTintList = ColorStateList.valueOf(Color.WHITE)

        rominfo.findViewById<ExpandableCardView>(R.id.card).backgroundColor =
            resources.getColor(R.color.cardBackground)
        rominfo.findViewById<TextView>(R.id.title).textColor = Color.WHITE
        rominfo.findViewById<ImageView>(R.id.arrow).imageTintList = ColorStateList.valueOf(Color.WHITE)

        val sp = applicationContext.getSharedPreferences("reloaded_pref", Context.MODE_PRIVATE)
        val lastCheck = resources.getString(R.string.last_check, sp.getString("last_check", ""))
        findViewById<TextView>(R.id.last_check).text = lastCheck

        scheduleJob()

        val onBootReceiver = ComponentName(application.packageName, BootReceiver::class.java.name)
        if (packageManager.getComponentEnabledSetting(onBootReceiver) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            packageManager.setComponentEnabledSetting(
                onBootReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

        check_update.setOnClickListener {
            if (Common.checkNetwork(baseContext)) {
                val extras = intent.extras
                if (extras != null) {
                    val response = extras.getSerializable("response") as Response
                    processResult(response)
                } else {
                    findViewById<ProgressBar>(R.id.progressBar).visibility = VISIBLE
                    toast(R.string.checking_update)
                    val checkUpdateTask = CheckUpdate(false, this)
                    checkUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                }

            } else {
                MaterialDialog(this@MainActivity).show {
                    icon(R.drawable.ic_no_wifi)
                    title(R.string.no_internet_title)
                    message(R.string.no_internet)
                    negativeButton(R.string.quit) { finish(); moveTaskToBack(true) }
                    onDismiss { finish(); moveTaskToBack(true) }
                }
                findViewById<MaterialCardView>(R.id.latest_build).visibility = GONE
                findViewById<MaterialCardView>(R.id.rominfo).visibility = GONE
            }
        }

        check_update.performClick()

        findViewById<TextView>(R.id.device_name).text = Common.getDevice(this)
        findViewById<TextView>(R.id.build_date).text = Common.getBuildDate(this)

    }

    private fun scheduleJob() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        if (jobScheduler.getPendingJob(0) == null) {
            val builder = JobInfo.Builder(0, ComponentName(this, OTAService::class.java))
            builder.setPeriodic(AlarmManager.INTERVAL_DAY)
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            builder.setPersisted(true)

            jobScheduler.schedule(builder.build())
        }
    }

    override val callbackContext: Context
        get() = this

    override fun processResult(response: Response) {
        val latestBuildButton = findViewById<Button>(R.id.latest_build_download)
        val latestBuildLink = response.latestBuildURL
        val latestBuild = response.latestBuild
        val latestBuildVersion = findViewById<TextView>(R.id.latest_build_version)

        val latestBuildParts = latestBuild!!.split('-')
        val latestBuildInfoText = resources.getString(R.string.latest_build_info, latestBuildParts[0], latestBuildParts[1], latestBuildParts[2], latestBuildParts[3])
        latestBuildVersion.text = latestBuildInfoText
        latestBuildVersion.visibility = VISIBLE
        latestBuildButton.setOnClickListener {
            MaterialDialog(this@MainActivity).show {
                title(R.string.are_you_sure)
                message(R.string.browser_window)
                positiveButton(R.string.yes) {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse(latestBuildLink)
                    startActivity(openURL)
                }
                negativeButton(R.string.cancel) { }
            }
        }
        latestBuildButton.visibility = VISIBLE

        val sp = applicationContext.getSharedPreferences("reloaded_pref", Context.MODE_PRIVATE)
        val lastCheck = resources.getString(R.string.last_check, sp.getString("last_check", ""))
        findViewById<TextView>(R.id.last_check).text = lastCheck

        if (response.deviceSupported == 1) {
            if (response.updateAvailable == 1) {
                val updateStatus = findViewById<TextView>(R.id.update_status)
                updateStatus.setText(R.string.update_is_available)
                findViewById<ProgressBar>(R.id.progressBar).visibility = GONE
                updateStatus.visibility = VISIBLE
                updateStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_update_available, 0, 0, 0)
                val latestBuildText = resources.getString(R.string.latest_build_download, response.latestBuild)
                MaterialDialog(this@MainActivity).show {
                    icon(R.drawable.ic_update_available)
                    title(R.string.update_available)
                    message(text = latestBuildText)
                    positiveButton(R.string.yes) {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(response.latestBuildURL)
                        startActivity(openURL)
                    }
                    negativeButton(R.string.cancel) { }
                }
                val notificationManager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notifyID = 1
                val id = "reloaded_update"
                val name = getString(R.string.channel)
                val description = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_LOW
                val mChannel = NotificationChannel(id, name, importance)
                mChannel.description = description
                notificationManager.createNotificationChannel(mChannel)

                val mBuilder = NotificationCompat.Builder(applicationContext, "Reloaded Updates")
                    .setSmallIcon(R.drawable.ic_update_available)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_message))
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setChannelId(id)

                val mIntent = Intent(applicationContext, MainActivity::class.java)
                mIntent.action = Intent.ACTION_MAIN
                mIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                mIntent.putExtra("response", response)
                val pendingIntent = PendingIntent.getActivity(
                    applicationContext, 0, mIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                mBuilder.setContentIntent(pendingIntent)
                notificationManager.notify(notifyID, mBuilder.build())
            } else {
                val updateStatus = findViewById<TextView>(R.id.update_status)
                updateStatus.setText(R.string.updated)
                findViewById<ProgressBar>(R.id.progressBar).visibility = GONE
                updateStatus.visibility = VISIBLE
                updateStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_updated, 0, 0, 0)
            }

            findViewById<TextView>(R.id.maintainer_name).text = response.maintainerName
            xda_thread.setOnClickListener {
                MaterialDialog(this@MainActivity).show {
                    title(R.string.are_you_sure)
                    message(R.string.browser_window)
                    positiveButton(R.string.yes) {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(response.xdaLink)
                        startActivity(openURL)
                    }
                    negativeButton(R.string.cancel) { }
                }
            }
        } else {
            findViewById<MaterialCardView>(R.id.latest_build).visibility = GONE
            findViewById<MaterialCardView>(R.id.rominfo).visibility = GONE
            MaterialDialog(this@MainActivity).show {
                icon(R.drawable.ic_warning)
                title(R.string.error)
                message(R.string.device_not_supported)
                negativeButton(R.string.quit) { finish(); moveTaskToBack(true) }
                onDismiss { finish(); moveTaskToBack(true) }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
