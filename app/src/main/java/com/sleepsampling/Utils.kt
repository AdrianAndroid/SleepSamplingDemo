package com.sleepsampling

import android.util.Log
import com.flannery.sleepsamplingdemo.BuildConfig

object Utils {
    fun a(value: Double, min: Double, max: Double): Double {
        if (value < min) {
            return min
        }
        return if (value <= max) value else max
    }

    fun b(arg1: Float, arg2: Float, arg3: Float): Float {
        if (arg1 < arg2) {
            return arg2
        }
        return if (arg1 <= arg3) arg1 else arg3
    }

    fun c(arg0: Int, arg1: Int, arg2: Int): Int {
        if (arg0 < arg1) {
            return arg1
        }
        return if (arg0 <= arg2) arg0 else arg2
    }

    fun d(arg1: Long, arg3: Long, arg5: Long): Long {
        if (arg1 < arg3) {
            return arg3
        }
        return if (arg1 <= arg5) arg1 else arg5
    }

    fun e(arg2: Double, arg4: Double): Boolean {
        return !java.lang.Double.isNaN(arg2)
                && !java.lang.Double.isNaN(arg4)
                && !java.lang.Double.isInfinite(arg2)
                && !java.lang.Double.isInfinite(arg4)
                && arg2 - arg4 < 0.001
    }
}

inline fun logSE(tag: () -> String = { "sleep_sampling" }, str: () -> String) {
    if (BuildConfig.DEBUG) Log.e(tag(), str.invoke())
}

inline fun logSD(tag: () -> String = { "sleep_sampling" }, str: () -> String) {
    if (BuildConfig.DEBUG) Log.d(tag(), str.invoke())
}

inline fun logSI(tag: () -> String = { "sleep_sampling" }, str: () -> String) {
    if (BuildConfig.DEBUG) Log.i(tag(), str.invoke())
}