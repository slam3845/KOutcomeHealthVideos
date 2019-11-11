package com.slam.koutcomehealthvideos

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.slam.koutcomehealthvideos.utiles.SharedPreferencesEx

/**
 * This is a singleton object which will be called prior to any UI activity
 * is launch (see <application> in AndroidManifest.xml)
 *
 * Created by slam on 09/13/2019.
 */
class ThisApp: Application() {
    private val _versionCode = -1
    private val _versionName = "1.0.0"

    /**
     * Must include ThisApp in AndroidManifest.xml, such that this application
     * object "ThisApp" will be automatically initialized at app launch.
     *
     * <application android:name=".ThisApp"></application>
     */
    init{
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        // Should the video time duration be retrieved and shown on the UI?
        // This could be changed in the SettingsActivity.
        // Note that: If it is true, the app will start up a little slower, because
        // the gallery list will have to fetch the time duration from the video clips.
        //
        shouldRetrieveVideoTimeDuration = SharedPreferencesEx.instance!!.shouldRetrieveVideoTimeDuration()
    }

    companion object {
        private val TAG = ThisApp::class.java!!.simpleName

        var instance:ThisApp? = null
            protected set

        var shouldRetrieveVideoTimeDuration = true    // = true by default.
        var _isDebugBuild:Boolean? = null

        val context: Context
            get() = instance!!.applicationContext

        val versionCode:Int
            get() = instance!!._versionCode

        val versionName:String
            get() = instance!!._versionName

        fun getStringFromResId(resId:Int):String {
            return context.resources.getString(resId)
        }

        /**
         * Check to see if it is a debug build.
         * In some cases, we can implement additional test feature to run ONLY in the DEBUG build.
         *
         * @return
         */
        var isDebugBuild:Boolean = false
            get() {

                val context = ThisApp.instance!!.applicationContext
                val isDebuggable = 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE

                if (ThisApp.isDebugBuild == null)
                {
                    try
                    {
                        val pm = context.packageManager
                        val pi = pm.getPackageInfo(context.packageName, 0)

                        val resultLogicalAND = pi.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
                        ThisApp.isDebugBuild = resultLogicalAND != 0
                        Log.d(TAG,
                            "*** [ApplicationInfo.FLAG_DEBUGGABLE = " +
                                    Integer.toBinaryString(ApplicationInfo.FLAG_DEBUGGABLE) +
                                    "] & [pi.applicationInfo.flags (" + pi.applicationInfo.flags + ") = " +
                                    Integer.toBinaryString(pi.applicationInfo.flags) + "] = " +
                                    Integer.toBinaryString(resultLogicalAND) + " ***"
                        )
                    }
                    catch (nnfe: PackageManager.NameNotFoundException) {
                        nnfe.printStackTrace()
                        ThisApp.isDebugBuild = false
                    }

                }
//                Log.d(TAG, "*** Build: " + (if (ThisApp.instance!!._isDebugBuild) "DEBUG" else "RELEASE") + " ***")
//                return ThisApp.instance!!._isDebugBuild!!
                Log.d(TAG, "*** Build: " + (if (ThisApp.isDebugBuild) "DEBUG" else "RELEASE") + " ***")
                return ThisApp.isDebugBuild!!
            }
    }
}