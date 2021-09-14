package de.uriegel.superfit.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Layout
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import de.uriegel.superfit.R
import de.uriegel.superfit.android.Service
import de.uriegel.superfit.databinding.ActivityMainBinding
import de.uriegel.superfit.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : ActivityEx(), NavigationView.OnNavigationItemSelectedListener, CoroutineScope {

    override val coroutineContext = Main

    // TODO: Velocity from location
    // TODO:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) { }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        layoutContainer = findViewById<ViewPager>(R.id.layoutContainer)
        val pagerId = 1
        with(layoutContainer) {
            adapter = PagerAdapter(supportFragmentManager)
            id = pagerId
        }

        binding.navigationView.setNavigationItemSelectedListener(this)

        if (checkPermissions())
            initialize()
    }

    private inner class PagerAdapter(fm: FragmentManager)
        : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0    -> ControlsFragment()
                else -> TracksFragment()
            }
        }

//        fun getFragmentForPosition(position: Int): Fragment {
//            val tag = makeFragmentName(pagerId, getItemId(position))
//            return supportFragmentManager.findFragmentByTag(tag)!!
//        }

//        private fun makeFragmentName(containerViewId: Int, id: Long) = "android:switcher:$containerViewId:$id"
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                    initialize()
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

        binding.drawerLayout.closeDrawer(GravityCompat.START)
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

    private fun initialize() {
        if (Service.isRunning)
            startActivity(Intent(this@MainActivity, DisplayActivity::class.java))

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val map = preferences.getString(PreferenceActivity.PREF_MAP, null)
        if (map == null)
            startActivity(Intent(this, PreferenceActivity::class.java))
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutContainer: ViewPager

    companion object {
        const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1000
    }
}
