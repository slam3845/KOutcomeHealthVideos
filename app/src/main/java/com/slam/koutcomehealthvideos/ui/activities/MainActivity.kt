package com.slam.koutcomehealthvideos.ui.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.slam.koutcomehealthvideos.R
import com.slam.koutcomehealthvideos.data.VideoData
import com.slam.koutcomehealthvideos.ui.main.MainFragment

/**
 * Created by slam on 09/13/2019.
 */
class MainActivity : AppCompatActivity() {   // , MainFragment.OnListFragmentInteractionListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    internal var dlgAbout: AlertDialog? = null

    /**
     * Get the fragment that is associate with this activity
     *
     * @return
     */
    val fragment: MainFragment?
        get() {
            val fragmentManager = supportFragmentManager
            if (fragmentManager != null) {
                val fragments = fragmentManager.fragments
                if (fragments != null) {
                    for (iX in fragments.indices.reversed()) {
                        val fragment = fragments[iX]
                        if (fragment != null) {
                            if (fragment is MainFragment) {
                                return fragment as MainFragment
                            }
                            break
                        }
                    }
                }
            }
            return null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            // On first launching the activity, attach the fragment.
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings  // On option menu item "Settings" selected.
            -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_about     // On option menu item "About" selected.
            -> {
                onCreateAboutDialog()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Called when the specific components in fragment_video_item is clicked.
     * (See the components which has the android:onClick="onVideoItemClicked")
     *
     * @param view
     */
    fun onVideoItemClicked(view: View) {
        /*
         * The videoData object stored in the component's tag is used.
         *
         */
        val videoData = view.tag as VideoData ?: return
        Log.d(TAG, "MainActivity.onVideoItemClicked: " + videoData.title)
        fragment!!.selectVideo(videoData)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    /**
     * Show the video title which is currently playing on the actionBar.
     *
     * @param title
     */
    fun setAppTitleText(title: String) {
        val actionBar = supportActionBar
        actionBar?.setTitle(title)
    }

    /**
     * Create ans show the About dialog.
     */
    fun onCreateAboutDialog() {

        val dlgAboutBuilder = AlertDialog.Builder(this)
        dlgAboutBuilder.setTitle("Options")
        val layoutInflater:LayoutInflater = LayoutInflater.from(applicationContext)

        val viewAboutDialog = layoutInflater.inflate(R.layout.about_dialog, null, false)

        dlgAboutBuilder.setView(viewAboutDialog)
        dlgAboutBuilder.setTitle(R.string.dialog_title_about)
        dlgAboutBuilder.setCancelable(true)
        dlgAboutBuilder.setPositiveButton(R.string.ok,
            DialogInterface.OnClickListener { dialog, id -> dlgAbout!!.dismiss() })
        dlgAbout = dlgAboutBuilder.create()
        dlgAbout!!.show()
    }

    /**
     * On clicking the email text, launch the email client and populate the
     * email address in the "To:" field
     *
     * @param view
     */
    fun onEmailClicked(view: View) {
        dlgAbout!!.dismiss()

        val objEmail = view.tag

        if (objEmail is String) {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "text/html"
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(objEmail))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }
    }

    /**
     * This MainFragment.OnListFragmentInteractionListener method is not used for now.
     * It could implement some additional actions if needed.
     *
     * Note: Instead the LiveData observer is used.
     *
     * @param videoData
     */
    fun onListFragmentInteraction(videoData: VideoData) {
        Log.e(TAG, "onListFragmentInteraction() with new videoData, title = " + videoData.title)
    }

}