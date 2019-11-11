package com.slam.koutcomehealthvideos.utiles

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager

//------------------------------------------------------+
//        | Small Screen | Normal Screen | Large Screen |
//--------+--------------+---------------+--------------|
// Height |     480      |      800      |    1024      |
// Width  |     320      |      480      |     600      |
//------------------------------------------------------+

/**
 * Created by slam on 09/13/2019.
 */
object DeviceUtils {
    /**
     * Get the device display metrics.
     *
     * Samsung 6S Edge:
     * density=4.0, width=1440, height=2560, scaledDensity=4.0, xdpi=522.514, ydpi=537.388
     *
     * Google Nexus 5X:
     * density=2.625, width=1080, height=1794, scaledDensity=2.625, xdpi=422.03, ydpi=424.069
     *
     * @param context
     * @return
     */
    fun getDeviceDisplayMetrics(context: Context): DisplayMetrics {
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        return displayMetrics
    }

    fun getDeviceDisplayWidthInPixels(context: Context): Int {
        return getDeviceDisplayMetrics(context).widthPixels
    }

    fun getDeviceDisplayHeightInPixels(context: Context): Int {
        return getDeviceDisplayMetrics(context).heightPixels
    }

    /**
     * Get the device display density which is one of the following values:
     *  * DENSITY_LOW = 120
     *  * **DENSITY_MEDIUM  = 160 (Default)**
     *  * DENSITY_HEIGHT  = 240
     *  * DENSITY_XHEIGHT = 320
     *  * DENSITY_TV      = 213
     *
     *
     * @param context
     * @return
     */
    fun getDeviceDensity(context: Context): Int {
        val displayMetrics = getDeviceDisplayMetrics(context)
        return displayMetrics.densityDpi
    }

    /**
     * Get the dimension of the screen (Left, Top, Right, Bottom).
     *
     * @param activity
     * @return
     */
    fun findPhysicalScreenDimension(activity: Activity?): Rect? {

        if (activity == null) {
            return null
        }
        val display = activity.windowManager.defaultDisplay

        return findPhysicalScreenDimentions(display)
    }

    /**
     * Get the dimension of the screen (Left, Top, Right, Bottom).
     *
     * @param context
     * @return
     */
    fun findPhysicalScreenDimension(context: Context?): Rect? {

        if (context == null) {
            return null
        }
        // If you don't have access to an activity to call getWindowManager on. You can use:
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

        return findPhysicalScreenDimentions(display)
    }

    /**
     *
     * @param display
     * @return
     */
    private fun findPhysicalScreenDimentions(display: Display): Rect {
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)

        // since SDK_INT = 1;
        var widthPixels = metrics.widthPixels
        var heightPixels = metrics.heightPixels

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = Display::class.java.getMethod("getWidth").invoke(display) as Int
                heightPixels = Display::class.java.getMethod("getHeight").invoke(display) as Int
            } catch (ignored: Exception) {
            }

        if (Build.VERSION.SDK_INT >= 17) {
            try {
                val size = Point()
                Display::class.java.getMethod("getSize", Point::class.java).invoke(display, size)
                widthPixels = size.x
                heightPixels = size.y
            } catch (ignored: Exception) {

            }

        }
        return Rect(0, 0, widthPixels, heightPixels)
    }

    /**
     *  * Small Screen:  320W x  480H
     *  * Normal Screen: 480W x  800H
     *  * Large Screen:  600W x 1024H
     *
     *
     * @param context
     * @return
     */
    fun isSmallScreenDevice(context: Context): Boolean {
        val displayMetrics = getDeviceDisplayMetrics(context)

        return if (displayMetrics.widthPixels < 400 || displayMetrics.heightPixels < 500) {
            true
        } else false
    }
}