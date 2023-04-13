package com.flannery.sleepsamplingdemo

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sleepsampling.AccelerometerHelper
import com.sleepsampling.SleepSamplingMonitor

class MainActivity : AppCompatActivity() {
    private var requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        val avgDecibel = findViewById<TextView>(R.id.avgDecibel)
        val accXYZ = findViewById<TextView>(R.id.accXYZ)

        findViewById<View>(R.id.startMonitor).setOnClickListener {
            SleepSamplingMonitor.startMonitor(this@MainActivity)
        }
        findViewById<View>(R.id.stopMonitor).setOnClickListener {
            SleepSamplingMonitor.stopMonitor()
        }
        SleepSamplingMonitor.onCycle = {
            runOnUiThread {
                val rollingAverage = SleepSamplingMonitor.rollingAverage
                val max = rollingAverage.getMax()
                val min = rollingAverage.getMin()
                val average = rollingAverage.getAverage()
                val sb = StringBuilder()
                sb.append("Average=").append(average).append("\n")
                sb.append("Max=").append(max).append("\n")
                sb.append("Min=").append(min).append("\n")
                avgDecibel.text = sb.toString()
                val lastX = AccelerometerHelper.INSTANCE.lastX
                val lastY = AccelerometerHelper.INSTANCE.lastY
                val lastZ = AccelerometerHelper.INSTANCE.lastZ
                val sbXyz = StringBuilder()
                sbXyz.append("lastX=").append(lastX).append("\n")
                sbXyz.append("lastY=").append(lastY).append("\n")
                sbXyz.append("lastZ=").append(lastZ).append("\n")
                accXYZ.text = sbXyz.toString()
            }
        }
    }
}