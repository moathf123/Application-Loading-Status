package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var textViewFileName: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var button: Button
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        notificationManager =
            ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelAll()
        textViewFileName = findViewById(R.id.textView_detail_fileName)
        textViewStatus = findViewById(R.id.textView_detail_status)
        button = findViewById(R.id.button)
        val extras = intent.extras
        if (extras != null) {

            status = extras.getString("EXTRA_DOWNLOAD_STATUS")!!
            textViewFileName.text = extras.getString("EXTRA_FILENAME")!!
            textViewStatus.text = status

            if (status == getString(R.string.successful))
                textViewStatus.setTextColor(getColor(R.color.green))
            else
                textViewStatus.setTextColor(getColor(R.color.red))
        } else {
            textViewStatus.text = getString(R.string.no_found_text)
            textViewFileName.text = getString(R.string.no_found_text)
        }
        button.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

    }

}
