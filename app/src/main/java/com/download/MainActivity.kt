package com.download

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
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        options.setOnCheckedChangeListener { _, checkedId ->
            customUrl.isEnabled = checkedId == R.id.custom
        }

        downloadData.setOnClickListener {
            when (options.checkedRadioButtonId) {
                R.id.glide -> download(getString(R.string.glide_url))
                R.id.retrofit -> download(getString(R.string.retrofit_url))
                R.id.project -> download(getString(R.string.project_url))
                R.id.custom -> {
                    customUrl.isEnabled = true
                    if (customUrl.text.toString()
                            .isNotEmpty() && URLUtil.isValidUrl(customUrl.text.toString())
                    )
                        download(customUrl.text.toString())
                    else {
                        downloadData.hasErrorDownload()
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
