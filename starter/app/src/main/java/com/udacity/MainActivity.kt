package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var toast: Toast
    var radioGroup: RadioGroup? = null
    lateinit var radioButton: RadioButton
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        radioGroup = findViewById(R.id.radioGroup)
        notificationManager =
            ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
        custom_button.setOnClickListener {
            val intSelectButton: Int = radioGroup!!.checkedRadioButtonId
            if (radioGroup!!.checkedRadioButtonId == -1) {
                toast = Toast.makeText(
                    applicationContext,
                    getString(R.string.choose_download),
                    Toast.LENGTH_LONG
                )
                toast.show()
            } else {
                custom_button.setState(ButtonState.Loading)
                radioButton = findViewById(intSelectButton)
                onRadioButtonClicked(radioButton)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            context?.let {
                notificationManager.sendNotification(
                    getString(R.string.notification_description),
                    it
                )
            }
            custom_button.setState(ButtonState.Completed)
            checkStatus(id!!)
        }
    }

    private fun onRadioButtonClicked(view: View) {

        when (view.id) {
            R.id.radioButton_main_glide -> download(URL_glide)

            R.id.radioButton_main_loadApp -> download(URL_loadApp)

            R.id.radioButton_main_retrofit -> download(URL_retrofit)
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL_loadApp =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_glide = "https://codeload.github.com/bumptech/glide/zip/master"
        private const val URL_retrofit = "https://codeload.github.com/square/retrofit/zip/master"

    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun checkStatus(downloadReference: Long): String {
        val myDownloadQuery = DownloadManager.Query()
        myDownloadQuery.setFilterById(downloadReference)
        val cursor: Cursor = downloadManager.query(myDownloadQuery)

        if (cursor.moveToFirst()) {

            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)

            var statusText = ""
            when (status) {
                DownloadManager.STATUS_FAILED -> statusText = "STATUS_FAILED"
                DownloadManager.STATUS_PAUSED -> statusText = "STATUS_PAUSED"
                DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
                DownloadManager.STATUS_RUNNING -> statusText = "STATUS_RUNNING"
                DownloadManager.STATUS_SUCCESSFUL -> statusText = "STATUS_SUCCESSFUL"
            }

            return statusText
        }
        return "No Status Found"
    }
}
