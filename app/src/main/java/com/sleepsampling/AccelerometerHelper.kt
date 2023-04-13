package com.sleepsampling

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_UI
import java.util.*
import kotlin.math.abs

class AccelerometerHelper : SensorEventListener {
    companion object {
        val INSTANCE by lazy { AccelerometerHelper() }
    }

    private val random = Random()
    private var sensor: Sensor? = null
    private var sensorManager: SensorManager? = null
    var lastX = 0f
    var lastY = 0f
    var lastZ = 0f
    private var jjjjjj = 0f
    private var kkkkk = 0f
    private var changeXYZ = 0f
    private var isInit = false

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { analyzeSensorEvent(it) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        logSI { "AccelerometerHelper onAccuracyChanged accuracy=$accuracy" }
    }

    private fun analyzeSensorEvent(event: SensorEvent) {
        var isLessThree = 1
        if (event.sensor.type == 1) {
            val values = event.values
            val valX = values[0]
            val valY = values[1]
            val valZ = values[2]
            val changeX: Float = abs(this.lastX - valX)
            val changeY: Float = abs(this.lastY - valY)
            val changeZ: Float = abs(this.lastZ - valZ)
            this.changeXYZ = changeX + changeY + changeZ
            this.kkkkk = changeX.coerceAtLeast(Math.max(changeY, changeZ))
            val isLessOne = if (this.changeXYZ < 1.0f) 0 else 1 // 变化小：0，变化大：1
            if (this.changeXYZ < 3.0f) {
                isLessThree = 0
            }

            // 比1小就是35.99000002f,比3小就是45.990002f,再大就是55.99002f
            this.jjjjjj =
                (if (isLessOne == 0) 35.990002f else (if (isLessThree == 0) 45.990002f else 55.990002f) + valX) + random.nextInt(
                    4).toFloat()
            this.lastX = valX
            this.lastY = valY
            this.lastZ = valZ
            logSI { "AccelerometerHelper analyzeSensorEvent lastX=$lastX lastY=$lastY lastZ=$lastZ" }
        }
    }

    fun init(context: Context) {
        if (!isInit) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
            sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            isInit = true
        }
    }

    fun registerListener() {
        sensorManager?.registerListener(this, sensor, SENSOR_DELAY_UI)
    }

    fun unRegisterListener() {
        sensorManager?.unregisterListener(this)
    }
}