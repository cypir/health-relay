package com.cypir.healthrelay.relation

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.cypir.healthrelay.entity.Contact
import com.cypir.healthrelay.entity.ContactData

data class ContactWithContactData(
        @Embedded
        var contact : Contact?= null,

        @Relation(
                parentColumn = "id",
                entityColumn = "contact_id",
                entity = ContactData::class
        )

        var contactData: List<ContactData> = listOf()
)
