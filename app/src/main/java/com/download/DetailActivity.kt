package com.download

import android.app.DownloadManager
import android.app.NotificationManager
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    companion object {
        const val FILE_ID = "FILE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.cancelNotifications()

        val fileID = intent.extras?.getLong(FILE_ID)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager


        val q = DownloadManager.Query().setFilterById(fileID!!)
        val c: Cursor = downloadManager.query(q)

        c.moveToFirst().let {

            repositoryName.text = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI))

            status.text =
                if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) "valid" else "fail"
        }
        backButton.setOnClickListener { onBackPressed() }
    }

}
