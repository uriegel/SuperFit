package de.uriegel.superfit.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val toggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBarMain.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close) { }
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        layoutContainer = findViewById(R.id.layoutContainer)
        with(layoutContainer) {
            adapter = PagerAdapter(supportFragmentManager)
            offscreenPageLimit = 2
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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
//            val i = Intent(this, PreferenceActivity::class.java)
//            startActivity(i)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    private inner class PagerAdapter(fm: FragmentManager)
        : FragmentStateAdapter(fm, lifecycle) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0    -> ControlsFragment()
                else -> TracksFragment()
            }
        }

        override fun getItemCount(): Int = 2
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutContainer: ViewPager2
}