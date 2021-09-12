package de.uriegel.superfit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.os.HandlerCompat
import androidx.core.view.isVisible
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.ActivityMainBinding
import de.uriegel.superfit.databinding.ActivityMapDownloadBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MapDownloadActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun download(view: View) {
        val progressHandler = HandlerCompat.createAsync(Looper.getMainLooper())
        launch {
            downloadMap(progressHandler)
        }
//        for (progressUpdate in progressChannel) {
//            println(progressUpdate)
//        }
    }

    private suspend fun downloadMap(progressHandler: Handler) {
        val progressBar = binding.progressBarMapDownload
        val btn = binding.button
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(binding.editTextMapDownload.text.toString())
                val file = url.file
                val index = file.lastIndexOf('/')
                val fileName = file.substring(index + 1)

                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val size = connection.getHeaderField("Content-Length").toLong()
                progressHandler.post {
                    progressBar.visibility = View.VISIBLE
                    progressBar.max = 100
                    progressBar.progress = 0
                    btn.isEnabled = false
                }

                val inputStream = url.openStream()
                val dis = DataInputStream(inputStream)

                val buffer = ByteArray(8*1024)
                var length: Int
                var currentProgress = 0L

                val fos = FileOutputStream(File(filesDir, fileName))
                while (dis.read(buffer).also { length = it } > 0) {
                    fos.write(buffer, 0, length)
                    currentProgress += length
                    progressHandler.post { progressBar.progress = (currentProgress * 100 / size).toInt() }
                }

            } catch (mue: MalformedURLException) {
                Log.e("download map", "malformed url error", mue)
            } catch (ioe: IOException) {
                Log.e("download map", "io error", ioe)
            } catch (se: SecurityException) {
                Log.e("download map", "security error", se)
            } catch (e: Exception) {
                Log.e("download map", "error", e)
            } finally {
                progressHandler.post {
                    progressBar.visibility = View.INVISIBLE
                    btn.isEnabled = true
                }
            }
        }
    }
    private lateinit var binding: ActivityMapDownloadBinding
}