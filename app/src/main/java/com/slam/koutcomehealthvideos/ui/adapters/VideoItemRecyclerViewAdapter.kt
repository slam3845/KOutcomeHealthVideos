package com.slam.koutcomehealthvideos.ui.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slam.koutcomehealthvideos.R
import com.slam.koutcomehealthvideos.ThisApp
import com.slam.koutcomehealthvideos.data.VideoData
import com.slam.koutcomehealthvideos.utiles.DateTimeUtils
import com.slam.koutcomehealthvideos.utiles.DownloadImageTask
import com.slam.koutcomehealthvideos.utiles.VideoUtils
import kotlinx.android.synthetic.main.fragment_video_item.view.*


/**
 * [RecyclerView.Adapter] that can display a [VideoData] and makes a call to the
 * specified [MainFragment.OnListFragmentInteractionListener].
 *
 * Created by slam on 09/13/2019.
 */
class VideoItemRecyclerViewAdapter (
    private val _listVideoData: List<VideoData>  // The video list to populate in the gallery list view.
) : RecyclerView.Adapter<VideoItemRecyclerViewAdapter.ViewHolder>() {

    companion object {
        private val TAG = VideoItemRecyclerViewAdapter::class.java.simpleName
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type
     * to represent an item.
     *
     * The RecyclerView’s adapter uses the ViewHolder pattern. ViewHolder Allow making
     * list scrolling act smoothly.  It store list row views references with calling the
     * findViewById() method only occurs a couple of times, rather than for the entire
     * dataset and on each bind view.
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_video_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the itemView to reflect the
     * item at the given position.
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.videoData = _listVideoData[position]

        // The video "sources" in the videos.json file could have multiple video URLs.
        // But to make it simple for this demo, we're only interested in the first video.
        //
        val videoSourceAndDuration = holder.videoData!!.listUrlVideoSourceAndDuration?.get(0)
        if (holder.videoData!!.urlThumbNail != null && !holder.videoData!!.urlThumbNail.isEmpty()) {
            DownloadImageTask(holder.ivThumbNail).execute(holder.videoData!!.urlThumbNail)
        } else {
            holder.ivThumbNail.setImageResource(R.drawable.ic_video_reel)
        }
        if (ThisApp.shouldRetrieveVideoTimeDuration) {
            getVideoTimeDuration(holder.videoData)
        }
        holder.tvDuration.setText(videoSourceAndDuration!!.strDuration)
        holder.tvTitle.setText(holder.videoData!!.title)
        holder.tvSubTitle.setText(holder.videoData!!.subTitle)
        holder.tvDescription.setText(holder.videoData!!.description)

        /*
         * Set the layout components' tag to store the VideoData object.
         * The tag will be retrieved in MainActivity.onVideoItemClicked() when the component is clicked
         *
         * (See the components which has the android:onClick="onVideoItemClicked")
         */
        holder.layoutThumbnail.tag = holder.videoData
        holder.llTextArea.tag = holder.videoData
        holder.ivThumbNail.tag = holder.videoData
        holder.tvTitle.tag = holder.videoData
        holder.tvSubTitle.tag = holder.videoData
        holder.tvDescription.tag = holder.videoData
    }

    /**
     * Get the time duration from the video clip.
     *
     * Example of "urlVideoSource"
     * - For online URL: https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
     * - For local: "big_buck_bunny" on local asset folder "raw" without file extension.
     *
     * @param videoData
     * @return
     */
    fun getVideoTimeDuration(videoData: VideoData?): String {
        if (videoData == null) {
            return ""
        }

        // The video "sources" in the videos.json file could have multiple video URLs.
        // But to make it simple for this demo, we're only interested in the first video.
        //
        val videoSourceAndDuration = videoData!!.listUrlVideoSourceAndDuration?.get(0)
        if (videoSourceAndDuration == null || videoSourceAndDuration!!.urlVideoSource == null) {
            return ""
        }
        if (!videoSourceAndDuration!!.strDuration.isEmpty()) {
            // The time duration has already been fetch, so there is no need to do it again.
            return videoSourceAndDuration!!.strDuration
        }

        if (videoData!!.isOnlineVideoSource) {
            videoSourceAndDuration!!.lDuration =
                VideoUtils.getOnlineVideoDuration(videoSourceAndDuration.urlVideoSource!!)
        } else {
            val packageName = ThisApp.context.getPackageName()
            val idRaw = ThisApp.context.getResources()
                .getIdentifier(videoSourceAndDuration!!.urlVideoSource, "raw", packageName)

            // String videoFile = "android.resource://" + packageName + "/" + R.raw.lets_meet_to_eat;
            val videoFile = "android.resource://$packageName/$idRaw"
            val videoUri = Uri.parse("android.resource://$packageName/raw/lets_meet_to_eat")

            videoSourceAndDuration!!.lDuration = VideoUtils.getLocalVideoDuration(videoUri)
        }
        videoSourceAndDuration!!.strDuration =
            DateTimeUtils.convertMillieToHMmSs(videoSourceAndDuration!!.lDuration)
        return videoSourceAndDuration!!.strDuration
    }

    override fun getItemCount(): Int {
        return _listVideoData.size
    }

    inner class ViewHolder
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     *
     * @param view
     */
        (view: View) : RecyclerView.ViewHolder(view) {
        val layoutThumbnail: View
        val llTextArea: View

        val ivThumbNail: ImageView
        val tvDuration: TextView
        val tvTitle: TextView
        val tvSubTitle: TextView
        val tvDescription: TextView
        var videoData: VideoData? = null

        init {
            layoutThumbnail = view.findViewById(R.id.rlThumbnail)
            tvDuration = view.findViewById(R.id.tvDuration)
            llTextArea = view.findViewById(R.id.llTextArea)
            ivThumbNail = view.findViewById(R.id.ivVideoThumbnail)
            tvTitle = view.findViewById(R.id.tvTitle)
            tvSubTitle = view.findViewById(R.id.tvSubTitle)
            tvDescription = view.findViewById(R.id.tvDescription)

            view.layoutVideoItem.setOnClickListener(
                View.OnClickListener { Log.d(TAG, "Video item clicked: ") }
            )
        }

        override fun toString(): String {
            return super.toString() + " '" + tvTitle.text + "';  '" + tvSubTitle.text + "'"
        }
    }
}