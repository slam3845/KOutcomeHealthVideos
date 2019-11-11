package com.slam.koutcomehealthvideos.data

import androidx.lifecycle.MutableLiveData
import com.slam.koutcomehealthvideos.Constants
import com.slam.koutcomehealthvideos.Constants.VIDEO_SOURCE_LOCAL
import com.slam.koutcomehealthvideos.Constants.VIDEO_SOURCE_ONLINE
import java.util.*

/**
 * Created by slam on 09/13/2019.
 */
class VideoData : MutableLiveData<VideoData> {

    var indexVideo: Int = 0 // 1, 2, 3, ...
    var localOrOnlineVideo: Int = 0
    var description: String
    var title: String
    var subTitle: String
    var urlThumbNail: String
    var listUrlVideoSourceAndDuration: MutableList<VideoSourceAndDuration>? = null

    val isOnlineVideoSource: Boolean
        get() = this.localOrOnlineVideo == VIDEO_SOURCE_ONLINE

    val isLocalVideoSource: Boolean
        get() = this.localOrOnlineVideo == VIDEO_SOURCE_LOCAL

    fun isOnline() : Boolean {
        return this.localOrOnlineVideo == VIDEO_SOURCE_ONLINE
    }

    inner class VideoSourceAndDuration {
        // Example of setting the videoSourceAndDuration.urlVideoSource:
        //  - For online URL: https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
        //
        //  - For local: "big_buck_bunny" on local asset folder "raw" without file extension.
        //
        var urlVideoSource: String? = null
        var lDuration: Long = 0
        var strDuration: String

        init {
            urlVideoSource = null
            lDuration = -1
            strDuration = ""
        }
    }

    constructor() {
        this.indexVideo = -1
        this.localOrOnlineVideo = Constants.VIDEO_SOURCE_UNKNOWN
        this.description = "N/A"
        this.title = "Unknown"
        this.subTitle = ""
        this.urlThumbNail = ""
        this.listUrlVideoSourceAndDuration = null
    }

    constructor(
        indexVideo: Int, videoType: Int, description: String, title: String, subTitle: String,
        urlThumbNail: String
    ) {
        this.indexVideo = indexVideo
        this.localOrOnlineVideo = videoType
        this.description = description
        this.title = title
        this.subTitle = subTitle
        this.urlThumbNail = urlThumbNail
    }

    /**
     * Example of "urlVideoSource"
     * - For online URL: https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
     * - For local: "big_buck_bunny" on local asset folder "raw" without file extension.
     *
     * @param urlVideoSource
     * @return
     */
    fun addVideoSourceUrl(urlVideoSource: String): List<VideoSourceAndDuration> {
        if (this.listUrlVideoSourceAndDuration == null) {
            this.listUrlVideoSourceAndDuration = ArrayList()
        }
        val videoSourceAndDuration = VideoSourceAndDuration()
        videoSourceAndDuration.urlVideoSource = urlVideoSource

        this.listUrlVideoSourceAndDuration!!.add(videoSourceAndDuration)

        // Compiling Error: Smart cast to 'MutableList<VideoData.VideoSourceAndDuration>'
        // is impossible, because 'listUrlVideoSourceAndDuration' is a mutable property
        // that could have been changed by this time.
        //
        // return listUrlVideoSourceAndDuration
        return listUrlVideoSourceAndDuration as MutableList<VideoSourceAndDuration>
    }

    /**
     *
     * @param localOrOnlineVideo
     *      Constants.VIDEO_SOURCE_UNKNOWN = 0;
     *      Constants.VIDEO_SOURCE_LOCAL = 1;
     *      Constants.VIDEO_SOURCE_ONLINE = 2;
     */
    fun setVideoSource(localOrOnlineVideo: Int) {
        this.localOrOnlineVideo = localOrOnlineVideo
    }
}
