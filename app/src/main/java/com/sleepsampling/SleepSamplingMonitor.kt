package com.sleepsampling

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.flannery.sleepsamplingdemo.BuildConfig
import java.util.*
import kotlin.math.log10

class SleepSamplingMonitor(val context: Context) : Handler.Callback {
    companion object {
        private const val MSG_START = 111
        private const val MSG_STOP = 112
        private const val MSG_CYCLE = 110

        private const val THREAD_NAME = "d_b_m_r"

        private const val BUFFER_SIZE = 2048
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

        var onCycle: (() -> Unit)? = null
        var rollingAverage: RollingAverage = RollingAverage(Int.MAX_VALUE)

        /**
         * 是否有RECORD_AUDIO权限
         */
        private fun isHasRecordAudioPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        }

        private var sleepSamplingMonitor: SleepSamplingMonitor? = null

        fun getContext(): Context? {
            return sleepSamplingMonitor?.context
        }

        fun startMonitor(context: Context) {
            if (!isHasRecordAudioPermission(context)) return
            if (sleepSamplingMonitor?.isRecording != true) {
                AccelerometerHelper.INSTANCE.init(context)
                sleepSamplingMonitor = SleepSamplingMonitor(context)
                sleepSamplingMonitor?.startMonitor()
            }
        }

        fun stopMonitor() {
            sleepSamplingMonitor?.stopMonitor()
            sleepSamplingMonitor = null
        }

        fun k0(arg4: Long): Boolean {
            if (1630598400000L > arg4) {  // 2021-09-03 00:00:00 必须在这个日期之后
                return false
            }
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = arg4
            val v4: Int = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
            return v4 in 381..1079
        }
    }

    private var record: AudioRecord? = null
    private var minBufferSize: Int? = null
    private var isRecording: Boolean = false
    private var shortArray: ShortArray? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_START -> {
                if (!isHasRecordAudioPermission(context)) {
                    return true
                }
                kotlin.runCatching {
                    startRecording()
                }
                if (getMinBufferSize() <= 0) {
                    return true
                }
                getBuffer()
                sendCycleMessage(2000) // 延迟开始
            }
            MSG_CYCLE -> {
                if (!isRecording) {
                    return true
                }
                val readBufferSize = readBuffer(getBuffer())
                if (readBufferSize < 0) {
                    startRecording()
                    return true
                }
                val decibel = getDecibel()
                rollingAverage.add(decibel)
                onCycle?.invoke()
                sendCycleMessage()
            }
            MSG_STOP -> {
                stopSleep()
                handler?.removeCallbacksAndMessages(null)
                handlerThread?.quitSafely()
                handler = null
                handlerThread = null
            }
        }


        return true
    }

    fun getCurRollingAverage(): RollingAverage {
        return rollingAverage
    }

    private fun sendStartMessage() {
        handler?.sendEmptyMessage(MSG_START)
    }

    private fun sendCycleMessage(delay: Long = 1000) {
        if (delay <= 0) {
            handler?.sendEmptyMessage(MSG_CYCLE)
        } else {
            handler?.sendEmptyMessageDelayed(MSG_CYCLE, delay)
        }
    }

    private fun startRecording() {
        kotlin.runCatching {
            getAudioRecord()?.startRecording()
        }
    }

    /**
     * 读取buffer
     */
    private fun readBuffer(buffer: ShortArray): Int {
        var recordSize = 0
        kotlin.runCatching {
            recordSize = getAudioRecord()?.read(buffer, 0, getMinBufferSize()) ?: 0
        }
        if (recordSize == 0) {
            kotlin.runCatching {
                Thread.sleep(100L, 0)
                recordSize = getAudioRecord()?.read(buffer, 0, getMinBufferSize()) ?: 0
            }
        }
        return recordSize
    }

    private fun getBuffer(): ShortArray {
        if (shortArray == null) {
            shortArray = ShortArray(getMinBufferSize())
        }
        return shortArray ?: ShortArray(getMinBufferSize())
    }

    /**
     * Buffer Size
     */
    private fun getMinBufferSize(): Int {
        if (minBufferSize == null) {
            minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        }
        return minBufferSize ?: 0
    }

    /**
     * 获取分贝
     */
    private fun getDecibel(): Double {
        val buffer = getBuffer()
        val bufferSize: Int = getAudioRecord()?.read(buffer, 0, buffer.size) ?: 0
        var sum = 0.0
        for (i in 0 until bufferSize) {
            sum += buffer[i] * buffer[i]
        }
        return (log10(sum / bufferSize.toDouble()) * 10.0)
    }

    private fun getAudioRecord(): AudioRecord? {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        if (record == null) {
            record = AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                BUFFER_SIZE)
        }
        return record
    }

    /**
     * 停止记录
     */
    private fun stopSleep() {
        kotlin.runCatching {
            record?.stop()
            record?.release()
            record = null
        }.onFailure {
            if (BuildConfig.DEBUG) {
                it.printStackTrace()
            }
        }
    }

    fun test() {
        AccelerometerHelper.INSTANCE.registerListener()
    }

    fun startMonitor() {
        handlerThread = HandlerThread(THREAD_NAME)
        handlerThread?.start()
        handlerThread?.looper?.let { looper ->
            handler = Handler(looper, this)
            sendStartMessage()
        }
        AccelerometerHelper.INSTANCE.registerListener()
        isRecording = true
    }

    fun stopMonitor() {
        isRecording = false
        AccelerometerHelper.INSTANCE.unRegisterListener()
        handler?.sendEmptyMessage(MSG_STOP)
    }
}