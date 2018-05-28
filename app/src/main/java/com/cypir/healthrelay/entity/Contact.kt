package com.cypir.healthrelay.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(
        tableName = "Contact")
data class Contact(
        @PrimaryKey(autoGenerate = true)
        val id : Long = 0,

        @ColumnInfo(name="first_name")
        val firstName : String,

        @ColumnInfo(name="last_name")
        val lastName : String,

        @ColumnInfo(name="phone_number")
        val phoneNumber : String,

        @ColumnInfo(name="enabled")
        val enabled : Boolean

)
