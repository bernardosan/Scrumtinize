package com.example.trellocloneapp.models

import android.os.Parcel
import android.os.Parcelable

data class Group(
    var title: String = "",
    val createdBy: String = "",
    var groupMembers: ArrayList<User> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
    parcel.readString()!!,
    parcel.readString()!!,
    parcel.createTypedArrayList(User.CREATOR)!!
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeTypedList(groupMembers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
