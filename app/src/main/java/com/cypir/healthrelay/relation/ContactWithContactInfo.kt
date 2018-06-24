package com.cypir.healthrelay.relation

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.cypir.healthrelay.entity.Contact
import com.cypir.healthrelay.entity.ContactInfo

data class ContactWithContactInfo(
        @Embedded
        var contact : Contact?= null,

        @Relation(
                parentColumn = "id",
                entityColumn = "contact_id",
                entity = ContactInfo::class
        )

        var contactInfo: List<ContactInfo> = listOf()
)
