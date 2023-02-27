package com.example.trellocloneapp.models

import android.os.Parcel
import android.os.Parcelable

data class Group(
    var documentId: String = "",
    var title: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assignedBoards: ArrayList<String> = ArrayList(),
    var groupMembersId: ArrayList<String> = ArrayList(),
    var groupMembersImage: ArrayList<String> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(documentId)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedBoards)
        parcel.writeStringList(groupMembersId)
        parcel.writeStringList(groupMembersImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }
}
