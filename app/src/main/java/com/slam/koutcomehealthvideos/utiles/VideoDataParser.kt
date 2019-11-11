package com.slam.koutcomehealthvideos.utiles

import android.util.Log
import com.slam.koutcomehealthvideos.Constants
import com.slam.koutcomehealthvideos.data.VideoData
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by slam on 09/13/2019.
 */
class VideoDataParser {

    companion object {
        private val TAG = VideoDataParser::class.java.simpleName
    }

    /**
     * Parse the json data store in the file.
     *
     * @param jsonData
     * @return
     */
    fun parse(jsonData: String): List<VideoData> {
        val listVideos = ArrayList<VideoData>()
        var videoData: VideoData? = null

        try {
            var idVideo = 0
            val jsonDataObject = JSONObject(jsonData)
            val jsonCategoryArray = jsonDataObject.optJSONArray("categories")
            val numVideoCategories = jsonCategoryArray!!.length()

            for (iX in 0 until numVideoCategories) {
                val jsonCategoryObject = jsonCategoryArray.get(iX) as JSONObject
                // String videoType = jsonCategoryObject.optString("name");

                /**
                 * VideosOnline
                 */
                var jsonVideosArray = jsonCategoryObject.optJSONArray("VideosOnline")
                var numVideos = jsonVideosArray!!.length()

                for (jX in 0 until numVideos) {
                    videoData = getVideoFromJsonData(
                        idVideo,
                        Constants.VIDEO_SOURCE_ONLINE,
                        jsonVideosArray.get(jX) as JSONObject
                    )
                    listVideos.add(videoData!!)
                    idVideo++
                }

                /**
                 * VideosLocal
                 */
                jsonVideosArray = jsonCategoryObject.optJSONArray("VideosLocal")
                numVideos = jsonVideosArray!!.length()

                for (jX in 0 until numVideos) {
                    videoData = getVideoFromJsonData(
                        idVideo,
                        Constants.VIDEO_SOURCE_LOCAL,
                        jsonVideosArray.get(jX) as JSONObject
                    )
                    listVideos.add(videoData!!)
                    idVideo++
                }
            }
        } catch (ex: JSONException) {
            Log.e(TAG, "parse(jsonData) JSONException encountered! " + ex.message)
            ex.printStackTrace()
        }

        return listVideos
    }

    /*
     * Example of video json data:
     * {
     *    "categories":[
     *      {
     *          "VideosLocal": [
     *             {
     *                   "sources":"LetsMeetToEat.mp4",
     *                   "title": "LetsMeetToEat"
     *             }
     *          ],
     *
     *          "VideosOnline":[
     *             {
     *                "description":"Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself..."
     *                "sources":[
     *                   "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
     *                ],
     *                "subtitle":"By Blender Foundation",
     *                "thumb":"https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
     *                "title":"Big Buck Bunny"
     *             },
     *          ]
     *       }
     *    ]
     * }
     */
    private fun getVideoFromJsonData(
        idVideo: Int,
        onlineOrLocal: Int,
        jsonObjVideos: JSONObject
    ): VideoData? {
        val videoData = VideoData()

        try {
            videoData.indexVideo = idVideo
            videoData.localOrOnlineVideo = onlineOrLocal
            videoData.description = jsonObjVideos.optString("description")
            videoData.title = jsonObjVideos.optString("title")
            videoData.subTitle = jsonObjVideos.optString("subtitle")
            videoData.urlThumbNail = jsonObjVideos.optString("thumb")

            val arrSources = jsonObjVideos.optJSONArray("sources")
            for (i in 0 until arrSources!!.length()) {
                // Example:
                //  - For online URL: https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
                //  - For local: "big_buck_bunny" on local asset folder "raw" without file extension.
                //
                val urlVideo = arrSources.get(i) as String
                videoData.addVideoSourceUrl(urlVideo)
            }
        } catch (ex: JSONException) {
            return null
        }
        return videoData
    }
}
