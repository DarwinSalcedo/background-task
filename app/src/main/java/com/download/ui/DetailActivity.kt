package com.download.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.download.R
import com.download.cancelNotifications
import com.download.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        const val FILE_ID = "FILE_ID"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.cancelNotifications()

        val fileID = intent.extras?.getLong(FILE_ID)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager


        val q = DownloadManager.Query().setFilterById(fileID!!)
        val c: Cursor = downloadManager.query(q)
        c.moveToFirst().let {

            binding.repositoryName.text =  c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI))
            binding.status.text = if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) getString(
                R.string.valid
            ) else getString(R.string.fail)

        }
        binding.backButton.setOnClickListener {
            onBackPressed() }
    }

}
