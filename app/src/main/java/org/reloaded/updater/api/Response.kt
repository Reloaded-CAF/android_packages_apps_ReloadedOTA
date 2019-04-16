package org.reloaded.updater.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Response(@Expose @SerializedName("device_supported") private val _deviceSupported: Int = 0,
               @Expose @SerializedName("update_available") private val _updateAvailable: Int = 0,
               @Expose @SerializedName("latest_build") private val _latestBuild: String? = "",
               @Expose @SerializedName("latest_build_download_url") private val _latestBuildURL: String? = "",
               @Expose @SerializedName("xda_link") private val _xdaLink: String? = "",
               @Expose @SerializedName("maintainer") private val _maintainerName: String? = ""): Serializable{


    val deviceSupported
        get() = _deviceSupported

    val updateAvailable
        get() = _updateAvailable

    val latestBuild
        get() = _latestBuild

    val latestBuildURL
        get() = _latestBuildURL

    val xdaLink
        get() = _xdaLink

    val maintainerName
        get() = _maintainerName

}
