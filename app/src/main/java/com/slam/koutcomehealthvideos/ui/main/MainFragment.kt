package com.slam.koutcomehealthvideos.ui.main

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slam.koutcomehealthvideos.R
import com.slam.koutcomehealthvideos.ThisApp
import com.slam.koutcomehealthvideos.data.VideoData
import com.slam.koutcomehealthvideos.ui.activities.MainActivity
import com.slam.koutcomehealthvideos.ui.adapters.VideoItemRecyclerViewAdapter
import com.slam.koutcomehealthvideos.utiles.DeviceUtils
import com.slam.koutcomehealthvideos.utiles.FileUtilsEx
import com.slam.koutcomehealthvideos.utiles.VideoDataParser
import kotlinx.android.synthetic.main.fragment_video_main.view.*

/**
 * Created by slam on 09/13/2019.
 */
class MainFragment : Fragment() {

    companion object {
        private val TAG = MainFragment::class.java.simpleName

        private val KEY_INDEX_VIDEO_ITEM_PLAYING = "KeyIndexVideoItemPlaying"
        private val KEY_VIDEO_POSTION = "KeyVideoPosition"

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    private var _mainViewModel: MainViewModel? = null

    private var _videoView: VideoView? = null
    private var _viewMainFragment: View? = null
    var listOfVideoData: List<VideoData>? = null
        private set

    private var _indexOfVideoPlaying = -1
    private var _videoPosition = 0


    /**
     * This interface must be implemented by activities that contain this fragment to
     * allow an interaction in this fragment to be communicated to the activity and
     * potentially other fragments contained in that activity.
     * <p/>
     * See the Android Training lesson
     * <a href="http://developer.android.com/training/basics/fragments/communicating.html">
     * Communicating with Other Fragments</a> for more information.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // For this demo, we assume the video list will not change.
        // So we only need to parse the video json file once.
        //
        if (listOfVideoData == null || listOfVideoData!!.size == 0) {
            val jsonVideoData = FileUtilsEx.readRawTextFile(activity!!.applicationContext, R.raw.videos)
            val videoDataParser = VideoDataParser()
            listOfVideoData = videoDataParser.parse(jsonVideoData!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewMainFragment = inflater.inflate(R.layout.fragment_video_main, container, false)

        resizePortraitLayoutsForScreenSize()

        val listView = _viewMainFragment!!.listViewVideos

        // Set the adapter
        if (listView is RecyclerView) {
            val context = listView.getContext()
            val recyclerView = listView as RecyclerView

            // recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = VideoItemRecyclerViewAdapter(listOfVideoData!!)
        }
        return _viewMainFragment
    }

    override fun onResume() {
        super.onResume()

        _videoView!!.seekTo(_videoPosition)
        _videoView!!.start()
    }

    /**
     * Change device orientation (Portrait <=> Landscape), onPause() will be called prior to
     * onSaveInstanceState(Bundle outState).
     *
     * VideoView.getCurrentPosition() will return correct value inside onPause().
     * But on some device (e.g. Samsung Note 4 with Android API 6.0.1 Marshmallow), it will
     * always return 0.  Therefore, to overcome this problem, we set the position here.
     */
    override fun onPause() {
        super.onPause()
        _videoPosition = _videoView!!.currentPosition
        Log.d(TAG, "MainFragment.onPause(): _videoPosition=$_videoPosition")
    }

    override fun onStop() {
        super.onStop()
        _videoView!!.pause()
    }

    /**
     * IMPORTANT:
     * On Samsung device running Android API 6.01 (Marshmallow), calling VideoView.getCurrentPosition()
     * inside onSaveInstanceState(Bundle outState) will return the correct value.
     *
     * But on Google Pixel 3 device (and others?) calling VideoView.getCurrentPosition() inside
     * onSaveInstanceState(Bundle outState) will ALWAYS return 0!!!  Maybe the video has already reset?
     *
     * Instead, calling VideoView.getCurrentPosition() inside onPause() will return the desired value.
     * @param outState
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // On some device (e.g. Samsung Note 4 with Android API 6.0.1 Marshmallow),
        // videoView.getCurrentPosition() will always return 0 when call inside
        // onSaveInstanceState().
        // However, call inside onPause() will return the correct value.
        // Calling sequence: onPause() => ... => onSaveInstanceState().
        //
        val thisVideoPosition = _videoView!!.currentPosition

        outState.putInt(KEY_VIDEO_POSTION, _videoPosition)
        outState.putInt(KEY_INDEX_VIDEO_ITEM_PLAYING, _indexOfVideoPlaying)
        Log.d(TAG, "MainFragment.onSaveInstanceState(): _videoPosition=$_videoPosition")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            _indexOfVideoPlaying = savedInstanceState.getInt(KEY_INDEX_VIDEO_ITEM_PLAYING)
            _videoPosition = savedInstanceState.getInt(KEY_VIDEO_POSTION)
            selectVideo(_indexOfVideoPlaying)
        }
    }

    private fun resizePortraitLayoutsForScreenSize() {
        val configInfo = resources.configuration
        if (configInfo.orientation == Configuration.ORIENTATION_PORTRAIT) {

            /**
             * At runtime, set the height of both the video player (1/3) and video list view (2/3)
             * accordingly for different Android device size/resolution.
             */
            val screenHeight = DeviceUtils.getDeviceDisplayHeightInPixels(activity!!)

            // Set the height of the VideoView to 1/3 of the screen height
            val layoutCardViewVideo = _viewMainFragment!!.layoutCardViewVideo
            val paramsCardView = layoutCardViewVideo.getLayoutParams()
            paramsCardView.height = (screenHeight / 3.0).toInt()
            layoutCardViewVideo.setLayoutParams(paramsCardView)

            // Set the height of the video RecycleView to take up the remaining screen height.
            val layoutListViewVideos = _viewMainFragment!!.layoutListViewVideos
            val paramsListViewVideos = layoutListViewVideos.getLayoutParams()
            paramsListViewVideos.height = screenHeight - paramsCardView.height
            layoutListViewVideos.setLayoutParams(paramsListViewVideos)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        try {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.d(TAG, "onConfigurationChanged(Configuration.ORIENTATION_LANDSCAPE)")

                // Display the video in full screen.
                val params = _videoView!!.layoutParams as android.widget.FrameLayout.LayoutParams
                params.width = DeviceUtils.getDeviceDisplayWidthInPixels(activity!!) + 100
                params.height = DeviceUtils.getDeviceDisplayHeightInPixels(activity!!) + 100
                _videoView!!.layoutParams = params
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.d(TAG, "onConfigurationChanged(Configuration.ORIENTATION_PORTRAIT)")
                val params = _videoView!!.layoutParams as android.widget.FrameLayout.LayoutParams
                params.width = DeviceUtils.getDeviceDisplayWidthInPixels(activity!!) + 100
                params.height = DeviceUtils.getDeviceDisplayHeightInPixels(activity!!) + 100
                _videoView!!.layoutParams = params
            }
        } catch (ex: Exception) {
            Log.e(TAG, "onConfigurationChanged() Exception caught: " + ex.message)
            ex.printStackTrace()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        /***********************************************************************
         * Observer:
         *
         * When the VideoData (LiveData) is updated with new video to play, this
         * observer will automatically call with the new videoData.
         *
         * See MainViewModel.setVideoData(VideoData videoData)
         */
        val observerVideoData = Observer<Any> { videoData ->
            if (videoData == null) {
                return@Observer
            }
            Log.d(TAG, "observeVideoLiveDataChange.onChange(): " + videoData)

            videoData.let {
                if (it is VideoData) {
                    val mVideoData : VideoData = it

                    // The video "sources" in the videos.json file could have multiple video URLs.
                    // But to make it simple for this demo, we're only interested in the first video.
                    //
                    val videoSourceAndDuration : VideoData.VideoSourceAndDuration?
                    videoSourceAndDuration = mVideoData.listUrlVideoSourceAndDuration?.get(0)

                    if (activity!! is MainActivity) {
                        (activity!! as MainActivity).setAppTitleText(mVideoData.title)
                    }

                    if (mVideoData.isOnlineVideoSource) {
                        // Example of online video url:
                        //      https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
                        //
                        _videoView?.setVideoURI(Uri.parse(videoSourceAndDuration?.urlVideoSource))
                    }
                    else {
                        // Video file stored on local asset raw folder.
                        // Example of local video name: "big_buck_bunny"
                        //
                        playLocalVideoClip(videoSourceAndDuration?.urlVideoSource!!)
                    }
                    _videoView!!.seekTo(_videoPosition)
                    _videoView!!.start()
                }
            }
        }

        // Observe the VideoData changes.
        _mainViewModel!!.videoData.observe(this, observerVideoData)

        _indexOfVideoPlaying = 0
        _mainViewModel!!.videoData = (listOfVideoData!![_indexOfVideoPlaying])

        setupVideoMediaPlayer()
    }

    /**
     *
     * @param urlVideoSource (e.g. "big_buck_bunny")
     */
    private fun playLocalVideoClip(urlVideoSource: String) {
        val packageName = ThisApp.context.getPackageName()
        val idRaw =
            ThisApp.context.getResources().getIdentifier(urlVideoSource, "raw", packageName)

        // String videoFile = "android.resource://" + packageName + "/" + R.raw.lets_meet_to_eat;
        val videoFile = "android.resource://$packageName/$idRaw"
        val videoUri = Uri.parse("android.resource://$packageName/raw/lets_meet_to_eat")
        _videoView!!.setVideoURI(videoUri)
    }

    fun setupVideoMediaPlayer() {
        // Previous version:
        // Launch the video clip in the default separate video player app.
        //
        // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.tutorialVideoURL)));

        // New version:
        // Launch the video clip in the embedded <VideoView> control inside the same activity.
        //
        _videoView = _viewMainFragment!!.findViewById(R.id.videoView) as VideoView

        val mediaController = MediaController(context)

        //  mediaController.setMediaPlayer(_videoView);
        mediaController.setAnchorView(_videoView)
        _videoView!!.setMediaController(mediaController)

        // The video to play will be set in MainFragment.onActivityCreated() where the
        // the MainViewModel is setup to observe the VideoData(LiveData) data changes
        // and the "(Observer<VideoData>).onChanged()" will be invoked where we will
        // set the video url to play.
        //
        // _videoView.setVideoPath(urlVideo);
        // _videoView.setVideoURI(Uri.parse(urlVideo));

        //        _videoView.seekTo(_videoPosition);
        _videoView!!.bringToFront()
        _videoView!!.setBackgroundColor(Color.TRANSPARENT)    // On some device, VideoView does not show video ????
        _videoView!!.requestFocus()
        _videoView!!.visibility = View.VISIBLE
        _videoView!!.start()
        _videoView!!.setZOrderOnTop(true)

        // video finish listener
        _videoView!!.setOnCompletionListener {
            try {
                Thread.sleep(1500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        _videoView!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (mediaController != null) {
                    if (mediaController.isShowing) {
                        mediaController.hide()
                    } else {
                        mediaController.show()
                    }
                }
            }
            true
        }

        // video finish listener
        _videoView!!.setOnCompletionListener {
            // On ending, select the next video to play.
            // If it is the last video in the list, it will loop back to the first video.
            _indexOfVideoPlaying = ++_indexOfVideoPlaying % listOfVideoData!!.size
            _videoPosition = 0
            selectVideo(_indexOfVideoPlaying)
            Log.d(TAG, ">>> Next Video: " + listOfVideoData!![_indexOfVideoPlaying].title)
        }
    }

    fun getVideoData(indexVideo: Int): VideoData {
        return listOfVideoData!![indexVideo]
    }

    fun selectVideo(item: Int) {
        val videoData = listOfVideoData!![item]
        _mainViewModel!!.videoData = videoData
    }

    fun selectVideo(videoData: VideoData) {
        _indexOfVideoPlaying = videoData.indexVideo
        _mainViewModel!!.videoData = videoData
    }
}
