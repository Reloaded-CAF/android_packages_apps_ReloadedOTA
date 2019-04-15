@file:Suppress("ConstantConditionIf")

package org.reloaded.updater

import android.app.AlarmManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.alespero.expandablecardview.ExpandableCardView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.info_layout.*
import org.reloaded.updater.api.Response
import org.reloaded.updater.tasks.CheckUpdate
import org.reloaded.updater.utils.Common
import org.reloaded.updater.utils.OTAService

class MainActivity : AppCompatActivity(), CheckUpdate.UpdateCheckerCallback {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scheduleJob()

        fab.setOnClickListener {

            if (Common.checkNetwork(baseContext)) {
                val rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
                fab.startAnimation(rotateAnim)

                val extras = intent.extras
                if (extras != null) {
                    val response = extras.getSerializable("response") as Response
                    processResult(response)
                }

                val checkUpdateTask = CheckUpdate(false, this)
                checkUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            } else{
                MaterialDialog(this@MainActivity).show {
                    icon(R.drawable.ic_no_wifi)
                    title(text = "No Internet Access")
                    message(text = "Turn on Cellular Data/WiFi to fetch updates!")
                    negativeButton(text = "Quit") { finish(); moveTaskToBack(true) }
                    onDismiss { finish(); moveTaskToBack(true) }
                }
                findViewById<ExpandableCardView>(R.id.latzip).visibility = GONE
                findViewById<Button>(R.id.lat_button).visibility = GONE
                findViewById<ExpandableCardView>(R.id.rominfo).visibility = GONE
            }
        }

        fab.performClick()

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

        val latestButton = findViewById<Button>(R.id.lat_button)
        val latestLink = response.latestBuildURL
        val linkText = response.latestBuild
        val latName = findViewById<TextView>(R.id.lat_name)

        latestButton.visibility = INVISIBLE
        latName.text = linkText
        latestButton.visibility = VISIBLE
        latestButton.setOnClickListener{
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(latestLink)
            startActivity(openURL)
        }

        if(response.deviceSupported == 1){
            if (response.updateAvailable == 1) {
                MaterialDialog(this@MainActivity).show {
                    icon(R.drawable.ic_update)
                    title(text = "Update available!")
                    message(text = "Latest Build: ${response.latestBuild}\nDownload?")
                    positiveButton(text = "Yes") {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(response.latestBuildURL)
                        startActivity(openURL)
                    }
                    negativeButton(text = "Cancel") { }
                }
            } else {
                MaterialDialog(this@MainActivity).show {
                    icon(R.drawable.ic_checkmark)
                    title(text = "You are up-to-date!")
                    negativeButton(text = "Close") { }
                }
            }

            findViewById<TextView>(R.id.device).text = android.os.Build.DEVICE
            findViewById<TextView>(R.id.builddt).text = Common.getBuildDate(this)
            findViewById<TextView>(R.id.maintainer_name).text = response.maintainerName
            xda_thread.setOnClickListener {
                MaterialDialog(this@MainActivity).show {
                    title(text = "Are You sure?")
                    message(text = "This will open a browser window")
                    positiveButton(text = "Yes") {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(response.xdaLink)
                        startActivity(openURL)
                    }
                    negativeButton(text = "Cancel") { }
                }
            }
            fab.clearAnimation()
        }else{
            findViewById<ExpandableCardView>(R.id.latzip).visibility = GONE
            findViewById<Button>(R.id.lat_button).visibility = GONE
            findViewById<ExpandableCardView>(R.id.rominfo).visibility = GONE
            MaterialDialog(this@MainActivity).show {
                icon(R.drawable.ic_warning)
                title(text = "Error!")
                message(text = "Your device is not officially supported.")
                negativeButton(text = "Quit") { finish(); moveTaskToBack(true) }
                onDismiss { finish(); moveTaskToBack(true) }
            }
            fab.clearAnimation()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
