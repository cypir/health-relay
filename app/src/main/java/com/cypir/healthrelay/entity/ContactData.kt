package com.cypir.healthrelay.entity

import android.arch.persistence.room.*

/**
 * Stores the individual contact methods
 */
@Entity(
    tableName = "Contact_Data",
    foreignKeys = [(ForeignKey(
            entity = Contact::class,
            parentColumns= arrayOf("id"),
            childColumns= arrayOf("contact_id"),
            onDelete = ForeignKey.CASCADE
    ))]
)
data class ContactData (
        /*
        This is the unique row id for a particular contact data row.
        This is provided by ContactsContract.data
         */
        @PrimaryKey
        var id : String = "",

        //denotes whether it is phone or email
        @ColumnInfo(name="type")
        var type : String = "",

        //populated when retrieving contact data from the contact picker
        @Ignore
        var info : String = "",

        //determines if this contact is enabled, meaning notifications will be sent to them automatically.
        var isEnabled : Boolean = false,

        @ColumnInfo(name="contact_id")
        var contactId : String = ""
)