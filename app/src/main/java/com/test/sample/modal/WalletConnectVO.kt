package com.test.sample.modal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class WalletConnectVO(
    val accountId: String?,
    val expiry: Long,
    val title: String,
    val sessionTopic: String
) : Parcelable {

    fun formatDate(): String {
        var result = ""
        val date = Date(expiry * 1000) // Convert seconds to milliseconds

        date.let {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            result = dateFormat.format(it)
        }
        return result
    }

}