package com.slam.koutcomehealthvideos.utiles

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView

/**
 * Created by slam on 09/13/2019.
 */
class DownloadImageTask(internal var _bmpImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

    companion object {
        private val TAG = DownloadImageTask::class.java.simpleName
    }

    /**
     * Download the image in an ASyncTask.
     *
     * @param urls
     * @return
     */
    override fun doInBackground(vararg urls: String): Bitmap? {
        val urldisplay = urls[0]
        var mIcon11: Bitmap? = null
        try {
            val `in` = java.net.URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (ex: Exception) {
            Log.e(TAG, "doInBackground() Exception: " + ex.message)
            ex.printStackTrace()
        }

        return mIcon11
    }

    override fun onPostExecute(result: Bitmap) {
        _bmpImage.setImageBitmap(result)
    }
}