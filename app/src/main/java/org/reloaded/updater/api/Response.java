package org.reloaded.updater.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("update_available")
    @Expose
    private Boolean update_available;

    @SerializedName("latest_build")
    @Expose
    private String latest_build;

    @SerializedName("latest_build_download_url")
    @Expose
    private String latest_build_download_url;

    public Boolean getUpdateAvailable() {
        return update_available;
    }

    public void setUpdateAvailable(Boolean update_available) {
        this.update_available = update_available;
    }

    public String getLatestBuild() {
        return latest_build;
    }

    public void setLatestBuild(String latest_build) {
        this.latest_build = latest_build;
    }

    public String getLatestBuildURL() {
        return latest_build_download_url;
    }

    public void setLatestBuildURL(String latest_build_download_url) {
        this.latest_build_download_url = latest_build_download_url;
    }
}
