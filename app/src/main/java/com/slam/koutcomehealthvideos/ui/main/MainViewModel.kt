package com.slam.koutcomehealthvideos.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.slam.koutcomehealthvideos.data.VideoData

/**
 * Created by slam on 09/13/2019.
 */
class MainViewModel : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }

    /**
     * Set the LiveData with the VideoData to play in the VideoView.
     * All the observers will be notified with the new VideoData change.
     * (e.g. See MainFragment.onActivityCreated())
     *
     * @param videoData
     */
    var videoData = VideoData()
        set(videoData) {
            if (this.videoData.hasObservers()) {
                Log.d(TAG, "MainViewModel.videoData has NO observer")
            }
            else {
                Log.d(TAG, "MainViewModel.videoData HAS observer")
            }
            this.videoData.setValue(videoData)
        }

    val title : String
        get() = videoData.title
}
