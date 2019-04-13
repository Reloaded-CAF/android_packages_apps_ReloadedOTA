package org.reloaded.updater.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("device_supported")
    @Expose
    private int device_supported;

    @SerializedName("update_available")
    @Expose
    private int update_available;

    @SerializedName("latest_build")
    @Expose
    private String latest_build;

    @SerializedName("latest_build_download_url")
    @Expose
    private String latest_build_download_url;

    @SerializedName("xda_link")
    @Expose
    private String xda_link;

    @SerializedName("maintainer")
    @Expose
    private String maintainer;

    public int getDeviceSupported() {
        return device_supported;
    }

    public int getUpdateAvailable() {
        return update_available;
    }

    public String getLatestBuild() {
        return latest_build;
    }

    public String getLatestBuildURL() {
        return latest_build_download_url;
    }

    public String getXDALink() {
        return xda_link;
    }

    public String getMaintainerName() {
        return maintainer;
    }
}
