package com.hfad.main

import android.os.Parcel
import android.os.Parcelable

data class Timer(val id: Int,
                 var msLeft: Long,
                 val wholeMs: Long,
                 var startTime: Long,
                 var isStarted: Boolean) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(msLeft)
        parcel.writeLong(wholeMs)
        parcel.writeLong(startTime)
        parcel.writeByte(if (isStarted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Timer> {
        override fun createFromParcel(parcel: Parcel): Timer {
            return Timer(parcel)
        }

        override fun newArray(size: Int): Array<Timer?> {
            return arrayOfNulls(size)
        }
    }


}
