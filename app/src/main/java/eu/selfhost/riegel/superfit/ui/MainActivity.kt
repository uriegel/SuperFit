package eu.selfhost.riegel.superfit.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.navigation.NavigationView
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.android.Service
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import org.jetbrains.anko.toast

class MainActivity : ActivityEx(), NavigationView.OnNavigationItemSelectedListener, CoroutineScope {

    override val coroutineContext = Main

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) { }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val pagerId = 1
        with(layoutContainer) {
            adapter = PagerAdapter(pagerId, supportFragmentManager)
            id = pagerId
        }

        navigationView.setNavigationItemSelectedListener(this)

        if (checkPermissions())
            initilize()
    }

    private inner class PagerAdapter(private val pagerId: Int, fm: FragmentManager?)
        : FragmentPagerAdapter(fm)
    {
        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0    -> ControlsFragment()
                else -> TracksFragment()
            }
        }

        fun getFragmentForPosition(position: Int): Fragment {
            val tag = makeFragmentName(pagerId, getItemId(position))
            return supportFragmentManager.findFragmentByTag(tag)!!
        }

        private fun makeFragmentName(containerViewId: Int, id: Long) = "android:switcher:$containerViewId:$id"
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                    initilize()
                else
                    toast("Some Permission denied")
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_controls -> layoutContainer.setCurrentItem(0, true)
            R.id.nav_tracks -> layoutContainer.setCurrentItem(1, true)
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.action_settings) {
            val i = Intent(this, PreferenceActivity::class.java)
            startActivity(i)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissions(): Boolean {
        val permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        return if (permissions.count() > 0) {
            requestPermissions(permissions.toTypedArray(), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
            false
        } else
            true
    }

    private fun initilize() {
        isInitialized = true

        if (Service.state == Service.ServiceState.Started)
            startActivity(Intent(this@MainActivity, DisplayActivity::class.java))

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val map = preferences.getString(PreferenceActivity.PREF_MAP, null)
        if (map == null)
            startActivity(Intent(this, PreferenceActivity::class.java))
    }

    private var isInitialized = false

    companion object {
        const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1000
        private const val CREATE_REQUEST_CODE = 40
    }
}
