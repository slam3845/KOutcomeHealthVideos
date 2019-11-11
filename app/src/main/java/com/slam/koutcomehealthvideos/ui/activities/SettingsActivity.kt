package com.slam.koutcomehealthvideos.ui.activities

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceCategory
import android.view.MenuItem
import com.slam.koutcomehealthvideos.R
import com.slam.koutcomehealthvideos.ThisApp
import com.slam.koutcomehealthvideos.utiles.SharedPreferencesEx

/**
 * Created by slam on 09/13/2019.
 */
class SettingsActivity : PreferenceActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    internal var _activitySharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_settings);

        // Import Note:
        // activity_settings.xml cannot be inflated using the inflater, because the inflater
        // does not understand with the tag <PreferenceScreen> Instead of setContentView(),
        // call addPreferencesFromResource().  However, addPreferencesFromResource() was
        // deprecated for API Level > 10.
        //
        // For API Level > 10, use PreferenceFragment instead.
        // https://developer.android.com/reference/android/preference/PreferenceActivity.html
        //
        // The non-deprecated approach is to use PreferenceFragment in conjunction with
        // PreferenceActivity, as is described in the PrefereceActivity documentation.
        // If your app is only supporting API Level 11 and higher, just use that.
        //
        // if (android.os.Build.VERSION.SDK_INT >= 11) { /* Honeycomd */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.layout.activity_settings)
        }
        val prefScreen = preferenceScreen
        _activitySharedPreferences = prefScreen.sharedPreferences
    }

    override fun onResume() {
        super.onResume()
        _activitySharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        _activitySharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
    }

    /**
     * This listener method will be called when any of the data is modified.
     *
     * @param sharedPreferences
     * @param keyName
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, keyName: String) {
        // The keyName is defined in the preference file (see activity_settings.xml)
        //
        if (isKey(keyName, R.string.pref_key_retrieve_video_duration_time)) {
            val keyRetrieveVideoDuration =
                ThisApp.getStringFromResId(R.string.pref_key_retrieve_video_duration_time)

            ThisApp.shouldRetrieveVideoTimeDuration =
                _activitySharedPreferences!!.getBoolean(keyRetrieveVideoDuration, true)
            SharedPreferencesEx.instance?.setRetrieveVideoTimeDuration(ThisApp.shouldRetrieveVideoTimeDuration)
        }
    }

    /**
     * This is a sanity check to make sure the key is indeed a valid
     * key defined in the preference file (see activity_settings.xml)
     *
     * @param keyName
     * @param prefKeyResId
     * @return
     */
    private fun isKey(keyName: String, prefKeyResId: Int): Boolean {
        val prefKeyName = ThisApp.getStringFromResId(prefKeyResId)
        return if (keyName == prefKeyName) {
            true
        } else false
    }

    private fun pickPreferenceObject(p: Preference) {
        if (p is PreferenceCategory) {
            for (i in 0 until p.preferenceCount) {
                pickPreferenceObject(p.getPreference(i))
            }
        } else {
            initSummary(p)
        }
    }

    private fun initSummary(p: Preference) {

        if (p is EditTextPreference) {
            p.setSummary(p.text)
        }
        // More logic for ListPreference, etc...
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
