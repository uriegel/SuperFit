package eu.selfhost.riegel.superfit.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.SoundEffectConstants
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.android.Service
import eu.selfhost.riegel.superfit.database.DataBase
import eu.selfhost.riegel.superfit.sensors.Bike
import eu.selfhost.riegel.superfit.sensors.HeartRate
import eu.selfhost.riegel.superfit.sensors.Searcher
import eu.selfhost.riegel.superfit.utils.getSdCard
import eu.selfhost.riegel.superfit.utils.serialize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File

class MainActivity : ActivityEx(), NavigationView.OnNavigationItemSelectedListener
{
	// TODO: in drawer, choose controls or track view
	// TODO: Track view activity
	// TODO: Then remove HTML-Titlebar

	@SuppressLint("SetJavaScriptEnabled")
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		setSupportActionBar(toolbar)

		val toggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		{
			override fun onDrawerOpened(drawerView: View)
			{
				super.onDrawerOpened(drawerView)
			}
		}

		drawerLayout.addDrawerListener(toggle)
		toggle.syncState()

		with(webView)
		{
			with(settings)
			{
				javaScriptEnabled = true
				domStorageEnabled = true
				allowFileAccessFromFileURLs = true
				allowUniversalAccessFromFileURLs = true
			}
			addJavascriptInterface(javaScriptInterface, "Native")
		}
		WebView.setWebContentsDebuggingEnabled(true)

		Service.setOnStateChangedListener { onStateChanged(it) }

		navigationView.setNavigationItemSelectedListener(this)

		if (checkPermissions())
			initilize()
	}

	override fun onBackPressed()
	{
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawer(GravityCompat.START)
		else
			super.onBackPressed()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
	{
		when (requestCode)
		{
			REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS ->
			{
				if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
					initilize()
				else
					toast("Some Permission denied")
			}
			else                                  -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean
	{
		// Handle navigation view item clicks here.
		when (item.itemId)
		{
			R.id.nav_controls -> webView.loadUrl("file:///android_asset/index.html#main")
			R.id.nav_tracks   -> webView.loadUrl("file:///android_asset/index.html#tracks")
		}

		drawerLayout.closeDrawer(GravityCompat.START)
		return true
	}

	private fun onStateChanged(state: Service.ServiceState) = webView.evaluateJavascript("onStateChanged(${state.serialize()})", null)

	private val javaScriptInterface = object
	{
		@JavascriptInterface
		fun getState() = doAsync { uiThread { onStateChanged(Service.state) } }

		@JavascriptInterface
		fun doHapticFeedback() = doAsync { uiThread { webView.playSoundEffect(SoundEffectConstants.CLICK) } }

		@JavascriptInterface
		fun fillTracks()
		{
			async(UI)
			{
				val tracks = DataBase.getTracksAsync().await()
				val gson = Gson()
				val json = gson.toJson(tracks)
				webView.evaluateJavascript("onTracks($json)", null)
			}
		}

		@JavascriptInterface
		fun fillMaps()
		{
			async(UI)
			{
				val sdCard: String = getSdCard()
				val mapsDir = "$sdCard/Maps"
				val directory = File(mapsDir)
				val files = directory.listFiles().filter { it.extension == "map" }.map { it.name }
				val gson = Gson()
				val json = gson.toJson(files)
				webView.evaluateJavascript("onMaps($json)", null)
			}
		}

		@Suppress("DEPRECATION")
		@JavascriptInterface
		fun onTrackSelected(trackNr: Long)
		{
			async(UI) {
				val intent = Intent(this@MainActivity, MapActivity::class.java)
				intent.putExtra(TRACK_NR, trackNr)
				val result = activityRequest(intent)
				if (result?.resultCode == Activity.RESULT_OK)
				{
					if (result.data?.getStringExtra(MapActivity.RESULT_TYPE) == MapActivity.RESULT_TYPE_DELETE)
						webView.evaluateJavascript("deleteTrack($trackNr)", null)
				}
			}
		}

		@JavascriptInterface
		fun start() = doAsync {
			uiThread {
				val startIntent = Intent(this@MainActivity, Service::class.java)
				startIntent.action = Service.ACTION_START
				startService(startIntent)
				startActivity(Intent(this@MainActivity, DisplayActivity::class.java))
			}
		}

		@JavascriptInterface
		fun stop() = doAsync {
			uiThread {
				val startIntent = Intent(this@MainActivity, Service::class.java)
				startIntent.action = Service.ACTION_STOP
				startService(startIntent)
				finish()
			}
		}

		@JavascriptInterface
		fun reset()
		{
			Searcher.stop()
			HeartRate.stop()
			Bike.stop()
			Searcher.start(this@MainActivity)
		}

		@JavascriptInterface
		fun display() = doAsync { uiThread { startActivity(Intent(this@MainActivity, DisplayActivity::class.java)) } }

		@JavascriptInterface
		fun finish() = doAsync { uiThread { forceOnBackPressed() } }
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (item.itemId == R.id.action_settings)
		{
			val i = Intent(this, PreferenceActivity::class.java)
			startActivity(i)
			return true
		}

		return super.onOptionsItemSelected(item)
	}

	private fun forceOnBackPressed() = super.onBackPressed()

	private fun checkPermissions(): Boolean
	{
		val permissions = listOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
			.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
		return if (permissions.count() > 0)
		{
			requestPermissions(permissions.toTypedArray(), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
			false
		}
		else
			true
	}

	private fun initilize()
	{
		webView.loadUrl("file:///android_asset/index.html#main")

		isInitialized = true

		if (Service.state == Service.ServiceState.Started)
			startActivity(Intent(this@MainActivity, DisplayActivity::class.java))

		val preferences = PreferenceManager.getDefaultSharedPreferences(this)
		val map = preferences.getString(PreferenceActivity.PREF_MAP, null)
		if (map == null)
			startActivity(Intent(this, PreferenceActivity::class.java))
	}

	private var isInitialized = false
	private lateinit var navView: WebView

	companion object
	{
		const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1000
		const val TRACK_NR = "TRACK_NR"
		private const val CREATE_REQUEST_CODE = 40
	}
}
