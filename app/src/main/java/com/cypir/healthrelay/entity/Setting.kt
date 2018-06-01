package com.cypir.healthrelay.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(
        tableName = "Setting")
data class Setting(
        @PrimaryKey(autoGenerate = true)
        val id : Long = 0,

        //interval to wait between no action and next action.
        @ColumnInfo(name="interval")
        val interval : Int
)