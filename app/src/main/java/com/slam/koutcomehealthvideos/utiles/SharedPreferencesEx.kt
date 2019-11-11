package com.slam.koutcomehealthvideos.utiles

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.slam.koutcomehealthvideos.R
import com.slam.koutcomehealthvideos.ThisApp

/**
 * Created by slam on 09/13/2019.
 */
class SharedPreferencesEx { // private constructor() {
    var sharedPreferences: SharedPreferences? = null

    init{
        instance = this
    }

    companion object {
        private val TAG = SharedPreferencesEx::class.java.simpleName

        // The default preference file used by the application is:
        //  mFile = "/data/data/com.outcomehealthvideos/shared_prefs/com.outcomehealthvideos.preferences.xml"
        //
        val OUTCOME_HEALTH_SHARED_PREFERENCES = "com.outcomehealthvideos"

        protected var _instance: SharedPreferencesEx? = null
            protected set

        var instance: SharedPreferencesEx? = null
            get() {
                if (_instance == null) {
                    _instance = SharedPreferencesEx()
                }
                // Smart cast to 'SharedPreferencesEx' is impossible, because '_instance' is
                // a mutable property that could have been changed by this time
                //
                // return _instance
                return _instance!!
            }
    }

    /*
     * See app's build.gradle
     *
     * android {
     *     versionCode 1
     *     versionName "1.0"
     *     ...
     * }
     *  See Constants.AppVersion.getBuildVersion() for getting versionCode
     *  from the running application instance.
     */
    var appVersionCode: Int
        get() {
            val keyAppVersionCode = ThisApp.getStringFromResId(R.string.pref_key_app_version_code)
            return this.getPrefInt(keyAppVersionCode, -1)
        }
        set(value) {
            val keyAppVersionCode = ThisApp.getStringFromResId(R.string.pref_key_app_version_code)
            this.setPrefInt(keyAppVersionCode, value)
        }

    var appVersionName: String?
        get() {
            val keyAppVersionName = ThisApp.getStringFromResId(R.string.pref_key_app_version_name)
            return this.getPrefString(keyAppVersionName, "")
        }
        set(value) {
            val keyAppVersionName = ThisApp.getStringFromResId(R.string.pref_key_app_version_name)
            this.setPrefString(keyAppVersionName, value!!)
        }

    init {
        sharedPreferences = ThisApp.instance?.getApplicationContext()?.getSharedPreferences(
            OUTCOME_HEALTH_SHARED_PREFERENCES, Context.MODE_PRIVATE
        )
    }

    fun debugCheckPreferenceData(message: String, showLog: Boolean) {
        if (ThisApp.isDebugBuild) {
            val versionCode = appVersionCode
            val versionName = appVersionName

            val retrieveVideoTimeDuration = this.shouldRetrieveVideoTimeDuration()

            if (showLog) {
                Log.d(
                    TAG, message +
                            "\nversionCode = " + versionCode +
                            "\nversionName = " + versionName +
                            "\nretrieveVideoTimeDuration = " + retrieveVideoTimeDuration
                )
            }
        }
    }


    fun cleanupOldData() {
        Log.d(TAG, "*** Clean up and delete all previous preference settings...")
        this.removeAllPreference()
    }

    fun removeSharedPreference(keyName: String) {
        if (sharedPreferences != null) {
            sharedPreferences!!.edit().remove(keyName).commit()
        }
    }

    fun removeAllPreference() {
        if (sharedPreferences != null) {
            sharedPreferences!!.edit().clear().commit()
        }
    }

    fun getPrefString(prefKey: String, defaultValue: String): String? {
        if (sharedPreferences != null) {
            try {
                return sharedPreferences!!.getString(prefKey, defaultValue)
            } catch (ex: Exception) {
                Log.e(TAG, "SharedPreferencesEx.getPrefString() Exception: " + ex.message)
                ex.printStackTrace()
            }

        }
        return defaultValue
    }

    fun setPrefString(prefKey: String, value: String) {
        if (sharedPreferences != null) {
            val editor = sharedPreferences!!.edit()
            editor.putString(prefKey, value)
            editor.commit()    // or editor.apply();
        }
    }

    fun getPrefInt(prefKey: String, defaultValue: Int): Int {
        try {
            if (sharedPreferences != null) {
                return sharedPreferences!!.getInt(prefKey, defaultValue)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "SharedPreferencesEx.getPrefInt() Exception: " + ex.message)
            ex.printStackTrace()
        }

        return defaultValue
    }

    fun setPrefInt(prefKey: String, value: Int) {
        if (sharedPreferences != null) {
            val editor = sharedPreferences!!.edit()
            editor.putInt(prefKey, value)
            editor.commit()
        }
    }

    fun getPrefFloat(prefKey: String, defaultValue: Float): Float {
        try {
            if (sharedPreferences != null) {
                return sharedPreferences!!.getFloat(prefKey, defaultValue)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "SharedPreferencesEx.getPrefFloat() Exception: " + ex.message)
            ex.printStackTrace()
        }

        return defaultValue
    }

    fun setPrefFloat(prefKey: String, value: Float) {
        if (sharedPreferences != null) {
            val editor = sharedPreferences!!.edit()
            editor.putFloat(prefKey, value)
            editor.commit()
        }
    }

    fun getPrefBool(prefKey: String, defaultValue: Boolean): Boolean {
        try {
            if (sharedPreferences != null) {
                return sharedPreferences!!.getBoolean(prefKey, defaultValue)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "SharedPreferencesEx.getPrefBool() Exception: " + ex.message)
            ex.printStackTrace()
        }

        return defaultValue
    }

    fun setPrefBool(prefKey: String, value: Boolean) {
        if (sharedPreferences != null) {
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(prefKey, value)
            editor.commit()
        }
    }

    fun setAppVersionCode(versionCode: String) {
        this.appVersionCode = Integer.valueOf(versionCode)
    }


    /******************************************************************************************
     * Beginning of Preference Page Settings
     *
     */

    /*
     * Preference Settings: To retrieve video time duration or not
     */
    fun shouldRetrieveVideoTimeDuration(): Boolean {
        val keyRetrieveVideoDuration =
            ThisApp.getStringFromResId(R.string.pref_key_retrieve_video_duration_time)
        return getPrefBool(keyRetrieveVideoDuration, true)
    }

    fun setRetrieveVideoTimeDuration(value: Boolean) {
        val keyRetrieveVideoDuration =
            ThisApp.getStringFromResId(R.string.pref_key_retrieve_video_duration_time)
        this.setPrefBool(keyRetrieveVideoDuration, value)
    }

    /**** End of Preference Page Settings  */
}