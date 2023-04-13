package com.sleepsampling

import android.util.Log
import com.flannery.sleepsamplingdemo.BuildConfig

class RollingAverage(val sampleSize: Int) {
    private val SIZE_DEFAULT = 1
    private var total = 0.0
    private var mMaxDb = 0.0
    private var mMinDb = 0.0
    private var mCurDb = 0.0
    private var mSize = SIZE_DEFAULT

    fun add(value: Double) {
        if (value == Double.NEGATIVE_INFINITY || value == Double.POSITIVE_INFINITY) {
            return
        }
        mCurDb = value
        // 最大值
        mMaxDb = mMaxDb.coerceAtLeast(value)
        // 最小值
        if (value < mMinDb || mSize == SIZE_DEFAULT) {
            mMinDb = value
        }
        if (total >= Int.MAX_VALUE - value || mSize >= sampleSize) {
            total = mMaxDb + mMinDb
            mSize = SIZE_DEFAULT
        } else {
            total += value
            mSize++
        }
        if (BuildConfig.DEBUG) {
            Log.i("RollingAverage", "mCurDb=$mCurDb mMaxDb=$mMaxDb mMinDb=$mMinDb total=$total")
        }
    }

    fun getAverage(): Double {
        return if (mSize <= 0) 0.0 else total / mSize
    }

    fun getMax(): Double {
        return mMaxDb
    }

    fun getMin(): Double {
        return mMinDb
    }

    fun getCurrentDb(): Double {
        return mCurDb
    }
}