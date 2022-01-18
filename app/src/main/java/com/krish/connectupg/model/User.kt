package com.krish.connectupg.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var username: String? = null,
    var email: String? = null,
    val uid: String? = null,
    val imageURL: String? = null
) : Parcelable
