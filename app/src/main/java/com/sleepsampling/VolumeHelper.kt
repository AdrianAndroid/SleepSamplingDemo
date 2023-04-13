package com.sleepsampling

class VolumeHelper {
    companion object {
        val TAG = "VolumeHelper"
        val KEY_BASE_DB = "key_base_db"
        val KEY_DELTA_DEEP = "key_delta_deep"
        val KEY_DELTA_LIGHT = "key_delta_light"
        val KEY_BASE_DB_DAYLIGHT = "key_base_db_daylight"
        val KEY_BASE_VOLUME = "key_base_volume"

        var decibel = 25.0f
        var daylight = 30.0f
        var DEEP = 2.0f
        var light = 22.0f

        val iiiiii = 5.0f
        val kkkkkk = 25.0f

        fun saveDecibel(decible: Float) {
            SPUtils.setFloat(KEY_BASE_DB, decible)
        }

        fun saveDaylight(daylight: Float) {
            SPUtils.setFloat(KEY_BASE_DB_DAYLIGHT, daylight)
        }
    }

    init {
        decibel = SPUtils.getFloat(KEY_BASE_DB, 25.0f)
        DEEP = SPUtils.getFloat(KEY_DELTA_DEEP, 2.0f)
        light = SPUtils.getFloat(KEY_DELTA_LIGHT, 22.0f)
        daylight = SPUtils.getFloat(KEY_BASE_DB_DAYLIGHT, 30.0f)
    }

    fun updateDecibel(decibel: Double) {
        val v3: Float = ((VolumeHelper.decibel + decibel) / 2.0f).toFloat()
        VolumeHelper.decibel = v3
        saveDecibel(v3)
    }

    fun updateDayLight(daylight: Float) {
        val v3: Float = ((VolumeHelper.daylight + daylight) / 2.0).toFloat()
        VolumeHelper.daylight = v3
        saveDaylight(v3)
    }

    fun analyze(vBaseDb: Int, avgDecibel: Float, startTime: Long): Int {
//        if (Utils.d(avgDecibel, -1.0)) {
//            Stages.EMPTY
//        }
        val initStage = Stages.AWAKE
        val v6 = vBaseDb.toFloat()
        if (isDeepStage(v6, avgDecibel, startTime)) {
            return Stages.DEEP
        }
        return if (isLightStage(v6, avgDecibel, startTime)) Stages.LIGHT else initStage
    }

    fun isDeepStage(arg0: Float, arg1: Float, arg2: Long): Boolean {
        return (if (arg1 > d(arg0, arg2)) 0 else 1) == 1
    }

    fun isLightStage(arg1: Float, arg2: Float, arg3: Long): Boolean {
        return (if (arg2 <= d(arg1, arg3) || arg2 > i(arg1, arg3)) 0 else 1) == 1
    }

    fun d(arg0: Float, arg1: Long): Float {
        return if (SleepSamplingMonitor.k0(arg1)) arg0 + iiiiii else arg0 + DEEP
    }

    fun i(arg0: Float, arg1: Long): Float {
        return if (SleepSamplingMonitor.k0(arg1)) arg0 + kkkkkk else arg0 + light
    }
}