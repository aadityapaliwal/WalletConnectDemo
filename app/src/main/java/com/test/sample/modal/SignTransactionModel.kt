package com.test.sample.modal

import android.os.Parcelable
import com.hedera.hashgraph.sdk.Transaction
import com.walletconnect.web3.wallet.client.Wallet
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SignTransactionModel(
    val txnBytes: ByteArray,
    val sessionRequest: @RawValue Wallet.Model.SessionRequest
) : Parcelable {

    val transaction: Transaction<*>
        get() {
        return Transaction.fromBytes(txnBytes)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SignTransactionModel

        if (!txnBytes.contentEquals(other.txnBytes)) return false
        if (sessionRequest != other.sessionRequest) return false

        return true
    }

    override fun hashCode(): Int {
        var result = txnBytes.contentHashCode()
        result = 31 * result + sessionRequest.hashCode()
        return result
    }

}