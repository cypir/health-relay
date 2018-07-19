package com.cypir.healthrelay.entity

import android.arch.persistence.room.*

/**
 * Stores the individual contact methods
 */
@Entity(
    tableName = "Contact_Data"
)
data class HRContactData (
        /*
        This is the unique row id for a particular contact data row.
        This is provided by ContactsContract.data
         */
        @PrimaryKey
        @ColumnInfo(name="id")
        var id : String = "",

        //denotes whether it is phone or email
        @ColumnInfo(name="mimetype")
        var mimetype : String = "",

        //populated when retrieving contact data from the contact picker
        @Ignore
        var data : String = "",

        //determines if this contact is enabled, meaning notifications will be sent to them automatically.
        var isEnabled : Boolean = false,

        //we store only the raw contact ids, since the contact_ids are subject to change
        //if merged.
        @ColumnInfo(name="raw_contact_id")
        var rawContactId : String = ""
)