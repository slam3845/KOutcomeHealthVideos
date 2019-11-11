package com.slam.koutcomehealthvideos.utiles

import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Created by slam on 09/13/2019.
 */
object FileUtilsEx {
    private val TAG = FileUtilsEx::class.java.simpleName

    /**
     * Read the the local text file which is stored in the asset folder
     * (e.g. "raw/video.txt")
     *
     * @param ctx
     * @param resId
     * @return
     */
    fun readRawTextFile(ctx: Context, resId: Int): String? {
        val inputStream = ctx.resources.openRawResource(resId)
        val byteArrayOutputStream = ByteArrayOutputStream()

        var numBytes: Int
        try {
            numBytes = inputStream.read()
            while (numBytes != -1) {
                byteArrayOutputStream.write(numBytes)
                numBytes = inputStream.read()
            }
            inputStream.close()
        } catch (e: IOException) {
            return null
        }

        return byteArrayOutputStream.toString()
    }
}