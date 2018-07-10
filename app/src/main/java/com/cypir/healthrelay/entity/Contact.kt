package com.cypir.healthrelay.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Ignore

/**
 * One Contact has many ContactData children.
 */
@Entity(
        tableName = "Contact"
)
data class Contact(

        //make this the contact id
        @PrimaryKey
        var id : String = "",

        @Ignore
        var name : String = ""
)
