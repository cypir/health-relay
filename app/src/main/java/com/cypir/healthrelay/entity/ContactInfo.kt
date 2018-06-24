package com.cypir.healthrelay.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Stores the individual contact methods
 */
@Entity(
    tableName = "ContactInfo",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Contact::class,
            parentColumns= arrayOf("id"),
            childColumns= arrayOf("contact_id"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class ContactInfo (
        //make this the contact id
        @PrimaryKey
        val id : String,

        //denotes whether it is phone or email
        @ColumnInfo(name="type")
        val type : String,

        //info can be phone number or email address
        @ColumnInfo(name="info")
        val info : String,

        @ColumnInfo(name="contact_id")
        var contactId : String ?= null
)