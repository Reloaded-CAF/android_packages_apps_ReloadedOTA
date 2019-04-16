package org.reloaded.updater.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Response: Serializable{

    @SerializedName("device_supported")
    @Expose
    var deviceSupported: Int = 0

    @SerializedName("update_available")
    @Expose
    var updateAvailable: Int = 0

    @SerializedName("latest_build")
    @Expose
    var latestBuild: String? = null

    @SerializedName("latest_build_download_url")
    @Expose
    var latestBuildURL: String? = null

    @SerializedName("xda_link")
    @Expose
    var xdaLink: String? = null

    @SerializedName("maintainer")
    @Expose
    var maintainerName: String? = null

    init {
        latestBuild = ""
        latestBuildURL = ""
        xdaLink = ""
        maintainerName = ""
    }

}
