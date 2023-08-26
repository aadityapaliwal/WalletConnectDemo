package com.test.sample.modal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WCTransactionModel(
    val accountId: String,
    val executable: String,
    val isTransaction: Boolean?
) : Parcelable {

}
