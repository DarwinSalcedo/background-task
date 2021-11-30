package com.download.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.core.content.ContextCompat
import com.download.R
import com.download.createChannel
import com.download.databinding.ActivityMainBinding
import com.download.sendNotification


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var scene: MotionScene

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val notificationManager = ContextCompat.getSystemService(
                context!!,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                id!!,
                context.getText(R.string.ready).toString(),
                context
            )

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.options.setOnCheckedChangeListener { _, checkedId ->
            binding.customUrl.isEnabled = checkedId == R.id.custom
        }

        binding.downloadData.setOnClickListener {
            when (binding.options.checkedRadioButtonId) {
                R.id.glide -> download(getString(R.string.glide_url))
                R.id.retrofit -> download(getString(R.string.retrofit_url))
                R.id.project -> download(getString(R.string.project_url))
                R.id.custom -> {
                    binding.customUrl.isEnabled = true
                    if (binding.customUrl.text.toString()
                            .isNotEmpty() && URLUtil.isValidUrl(binding.customUrl.text.toString())
                    )
                        download(binding.customUrl.text.toString())
                    else {
                        binding.downloadData.hasErrorDownload()
                        Toast.makeText(this, getString(R.string.insert_url), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        val notificationManager = getSystemService(NotificationManager::class.java)

        notificationManager.createChannel(
            getString(R.string.download_channel_id),
            getString(R.string.download_channel_name),
            getString(R.string.download_description)
        )
    }


    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }


}
