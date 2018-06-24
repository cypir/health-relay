package com.cypir.healthrelay.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * One Contact has many ContactInfo children.
 */
@Entity(
        tableName = "Contact"
)
data class Contact(

        //make this the contact id
        @PrimaryKey
        val id : String = "",

        @ColumnInfo(name="name")
        val name : String
)
