package com.slam.koutcomehealthvideos.utiles

import android.media.MediaMetadataRetriever
import android.net.Uri
import com.slam.koutcomehealthvideos.ThisApp
import java.util.HashMap

/**
 * Created by slam on 09/13/2019.
 */
object VideoUtils {
    /**
     * For online video URL:
     *
     * @param urlVideo (e.g. "https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
     * @return
     */
    fun getOnlineVideoDuration(urlVideo: String): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(urlVideo, HashMap())

        val timeInMillisec = retrieveTimeDuration(retriever)
        retriever.release()

        // String videoDuration = convertMillieToHMmSs(timeInMillisec);
        return timeInMillisec
    }

    /**
     * Uri videoUri = Uri.parse("android.resource://" + packageName +"/raw/R.raw.big_buck_bunny");
     *
     * For local - "big_buck_bunny" on local asset folder "raw" without file extension:
     * String packageName = ThisApp.getContext().getPackageName();
     * Uri videoUri = Uri.parse("android.resource://" + packageName +"/raw/big_buck_bunny");
     * long strDuration = VideoUtils.getLocalVideoDuration(videoUri);
     *
     * @param videoUri (e.g. "big_buck_bunny" on local asset folder "raw" without file extension)
     * @return
     */
    fun getLocalVideoDuration(videoUri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(ThisApp.context, videoUri)

        val timeInMillisec = retrieveTimeDuration(retriever)
        retriever.release()

        // String videoDuration = convertMillieToHMmSs(timeInMillisec);
        return timeInMillisec
    }

    /**
     *
     * @param retriever
     * @return
     */
    private fun retrieveTimeDuration(retriever: MediaMetadataRetriever): Long {
        val timeString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

        return if (timeString != null) {
            java.lang.Long.parseLong(timeString)
        } else 0
    }
}