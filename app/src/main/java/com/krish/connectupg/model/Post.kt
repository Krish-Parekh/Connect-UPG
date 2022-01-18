package com.krish.connectupg.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Post(
    var title: String? = null,
    var content: String? = null,
    var postTime: Long? = null,
    var uid:String?= null
) : Parcelable
