package com.test.sample.common

import android.util.Base64
import com.hedera.hashgraph.sdk.AccountBalanceQuery
import com.hedera.hashgraph.sdk.AccountId
import com.hedera.hashgraph.sdk.AccountInfoQuery
import com.hedera.hashgraph.sdk.AddressBookQuery
import com.hedera.hashgraph.sdk.Client
import com.hedera.hashgraph.sdk.FileId
import com.hedera.hashgraph.sdk.NodeAddressBook
import com.hedera.hashgraph.sdk.PrivateKey
import com.hedera.hashgraph.sdk.PublicKey
import com.hedera.hashgraph.sdk.Transaction
import com.hedera.hashgraph.sdk.TransactionId
import com.hedera.hashgraph.sdk.TransactionReceiptQuery
import com.hedera.hashgraph.sdk.TransactionResponse
import com.test.sample.App
import com.test.sample.R

object HederaHashgraphManager {

    var networkName = "testnet"

    private fun operatorAccountId() : AccountId {
        return AccountId.fromString(App.instance.applicationContext.getString(R.string.operator_acc_id))
    }

    /// Private key for the operator to use in this example.
    private fun operatorKey() : PrivateKey {
        return PrivateKey.fromString(App.instance.applicationContext.getString(R.string.operator_pvt_key))
    }

    fun publicKey(): PublicKey {
        return PublicKey.fromString(App.instance.applicationContext.getString(R.string.operator_public_key))
    }

    fun getClient() : Client {
        val client = Client.forName(networkName)
        client.setOperator(operatorAccountId(), operatorKey())
        return client
    }

    fun getAccountBalance(onSuccess: (String?) -> Unit) {
        AccountBalanceQuery().setAccountId(operatorAccountId()).executeAsync(getClient()).thenAccept {
            onSuccess(it.toString())
        }
    }

    fun getAccountInfo(bytes: ByteArray, onSuccess: (String?) -> Unit) {
        try {
            val accountID = AccountId.fromBytes(bytes)
            val client = getClient()
            val accountInfo = AccountInfoQuery().setAccountId(accountID).execute(client)
            onSuccess(accountInfo.toBytes().toBase64String())
            client.close()
        } catch (e: Exception) {
            onSuccess(null)
        }
    }

    fun getTransactionReceiptInfo(txnIdValue: ByteArray, onSuccess: (String?) -> Unit) {
        try {
            val txnId = TransactionId.fromBytes(txnIdValue)
            TransactionReceiptQuery().setTransactionId(txnId).executeAsync(getClient()).thenAccept {
                onSuccess(it.toBytes().toBase64String())
            }
        } catch (e: Exception) {
            onSuccess(null)
        }
    }

    fun getMirrorNetwork() : List<String> {
        return getClient().mirrorNetwork
    }

    fun getNetwork() : NodeAddressBook {
        return AddressBookQuery().setFileId(FileId.ADDRESS_BOOK).executeAsync(Client.forName(networkName)).get()
    }

    fun getSignedTransaction(transaction: Transaction<*>) : Transaction<*>{
        return transaction.sign(operatorKey())
    }

    fun executeTransaction(transaction: Transaction<*>, isSigned: Boolean) : TransactionResponse {
        val response = if (isSigned) {
            transaction.execute(getClient())
        } else {
            transaction.sign(operatorKey()).execute(getClient())
        }
        return response
    }

    fun ByteArray.toBase64String(): String {
        return Base64.encodeToString(this, Base64.NO_WRAP)
    }

}