package com.cypir.healthrelay.pojo

import android.net.Uri

data class HRContact(
        var rawContactId : String,
        var contactId : String,
        var contactDataId : String,
        var displayName : String,
        var thumbnailUri : Uri?
)