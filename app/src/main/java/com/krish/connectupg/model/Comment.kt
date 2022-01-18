package com.krish.connectupg.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    var username : String? = null,
    var content: String? = null,
    var uid: String? = null,
    var postKey: Long? = null,
    var commentTime:Long? = null,
):Parcelable
