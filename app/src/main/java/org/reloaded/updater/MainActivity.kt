package org.reloaded.updater

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.alespero.expandablecardview.ExpandableCardView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.reloaded.updater.api.ApiInterface
import org.reloaded.updater.api.ApiClient
import org.reloaded.updater.api.Response
import retrofit2.Call
import retrofit2.Callback
import android.provider.Settings
import android.util.Log
import android.view.View.*

class MainActivity : AppCompatActivity() {

    private var networkAvail = false
    var apiInterface: ApiInterface? = null
    var updateresponse: Response?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnClickListener {
            checkNetwork()

            if (networkAvail) {
                val rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
                fab.startAnimation(rotateAnim)
                apicall()
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
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)

    }

    // Checks internet access
    private fun checkNetwork(){
        val connected = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netinfo = connected.activeNetworkInfo
        networkAvail = netinfo != null && netinfo.isConnected
    }

    // Displays Update availability
    private fun updateReq() {
        doAsync {
            uiThread {
                if(updateresponse!!.deviceSupported == 1){
                    if (updateresponse!!.updateAvailable == 1) {
                        MaterialDialog(this@MainActivity).show {
                            icon(R.drawable.ic_update)
                            title(text = "Update available!")
                            message(text = "Latest Build: ${updateresponse!!.latestBuild}\nDownload?")
                            positiveButton(text = "Yes") {
                                val openURL = Intent(Intent.ACTION_VIEW)
                                openURL.data = Uri.parse(updateresponse!!.latestBuildURL)
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
                    findViewById<TextView>(R.id.builddt).text = getBuildDate()
                    findViewById<TextView>(R.id.maintainer_name).text = updateresponse?.maintainerName
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
        }
    }

    private fun getBuildDate() : String{
        val version = SystemPropertiesProxy.get(applicationContext,"ro.reloaded.version")
        if(version != null) {
            return version.split("-")[3]
        }else{
            return "20190101"
        }
    }

    // Displays latest zip link
    private fun getLink() {
        doAsync {
            val latestButton = findViewById<Button>(R.id.lat_button)
            val latestLink = updateresponse?.latestBuildURL
            val linktext = updateresponse?.latestBuild
            val latName = findViewById<TextView>(R.id.lat_name)

            uiThread {
                latestButton.visibility = INVISIBLE

                latName.text = linktext

                latestButton.visibility = VISIBLE

                latestButton.setOnClickListener{
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse(latestLink)
                    startActivity(openURL)
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun apicall(){
        doAsync {
            uiThread {
                val device = android.os.Build.DEVICE
                val builddate = getBuildDate()
                val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                val call = apiInterface?.checkupdates(device, androidId, builddate)
                toast("Checking for updates!")
                call?.enqueue(object : Callback<Response> {
                    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                        if (response.body() != null) {
                            updateresponse = response.body()
                            getLink()
                            updateReq()
                        }
                    }

                    override fun onFailure(call: Call<Response>, t: Throwable) {
                        //
                    }
                })
            }
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
