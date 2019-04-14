package org.reloaded.updater.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response {
    @SerializedName("device_supported")
    @Expose
    val deviceSupported: Int = 0

    @SerializedName("update_available")
    @Expose
    val updateAvailable: Int = 0

    @SerializedName("latest_build")
    @Expose
    val latestBuild: String? = null

    @SerializedName("latest_build_download_url")
    @Expose
    val latestBuildURL: String? = null

    @SerializedName("xda_link")
    @Expose
    val xdaLink: String? = null

    @SerializedName("maintainer")
    @Expose
    val maintainerName: String? = null
}
