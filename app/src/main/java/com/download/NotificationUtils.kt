package com.download

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.download.ui.DetailActivity
import com.download.ui.DetailActivity.Companion.FILE_ID
import com.download.ui.MainActivity


private val NOTIFICATION_ID = 0

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(
    fileId: Long,
    messageBody: String,
    applicationContext: Context
) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val fileDownloadedImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_baseline_cloud_download_24
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(fileDownloadedImage)
        .bigLargeIcon(null)


    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra(FILE_ID, fileId)
    detailIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

    val detailPendingIntent = TaskStackBuilder.create(applicationContext).run {
        addNextIntentWithParentStack(detailIntent)
            .addParentStack(MainActivity::class.java)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_channel_id)
    )

        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setStyle(bigPicStyle)
        .setLargeIcon(fileDownloadedImage)
        .addAction(
            R.drawable.ic_baseline_file_open_24,
            applicationContext.getString(R.string.notification_button),
            detailPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}


fun NotificationManager.cancelNotifications() {
    cancelAll()
}

fun NotificationManager.createChannel(channelId: String, channelName: String, description: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
            .apply {
                setShowBadge(true)
            }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.enableVibration(true)
        notificationChannel.description = description

        this.createNotificationChannel(notificationChannel)

    }
}
