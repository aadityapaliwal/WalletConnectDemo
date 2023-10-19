package com.test.sample.common

import android.util.Base64
import com.hedera.hashgraph.sdk.AccountBalance
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

    private fun getClient() : Client? {
        try {
            val client = Client.forName(networkName)
            client.setOperator(operatorAccountId(), operatorKey())
            return client
        }catch (e: Throwable) {
            AppLog.e("HederaHashgraphManager", "Error in getClient object creation " + e.message)
        }
        return null
    }

    fun getAccountBalance(onSuccess: (AccountBalance?, String?) -> Unit) {
        try {
            getClient().let { client ->
                if (client != null) {
                    AccountBalanceQuery().setAccountId(operatorAccountId()).executeAsync(client)
                        .thenAccept {
                            onSuccess(it, null)
                            client.close()
                        }
                } else {
                    onSuccess(null, "Error")
                }
            }
        } catch (e: Exception) {
            onSuccess(null, e.message)
        }
    }

    fun getAccountInfo(bytes: ByteArray, onSuccess: (String?, String?) -> Unit) {
        try {
            getClient().let { client ->
                if (client != null) {
                    val accountID = AccountId.fromBytes(bytes)
                    val accountInfo = AccountInfoQuery().setAccountId(accountID).executeAsync(client).get()
                    AppLog.d("getAccountInfo", accountInfo.toString())
                    onSuccess(accountInfo.toBytes().toBase64String(), null)
                    client.close()
                } else {
                    onSuccess(null, "Error")
                }
            }
        } catch (e: Exception) {
            AppLog.d("getAccountInfo", e.toString())
            onSuccess(null, e.message)
        }
    }

    fun getTransactionReceiptInfo(txnIdValue: ByteArray, onSuccess: (String?, String?) -> Unit) {
        try {
            getClient().let { client ->
                if (client != null) {
                    val txnId = TransactionId.fromBytes(txnIdValue)
                    AppLog.d("HederaHashgraphManager", "getTransactionReceiptInfo Calling TxnID: $txnId")
                    TransactionReceiptQuery().setTransactionId(txnId).executeAsync(client).thenAccept {
                        AppLog.d("HederaHashgraphManager", "Success getting Transcation Rec $it")
                        onSuccess(it.toBytes().toBase64String(), null)
                        client.close()
                    }
                } else {
                    onSuccess(null, "Error")
                }
            }
        } catch (e: Exception) {
            AppLog.d("HederaHashgraphManager", "Error in getTransactionReceiptInfo ${e}")
            onSuccess(null, e.message)
        }
    }

    fun getMirrorNetwork() : List<String>? {
        return try {
            getClient()?.mirrorNetwork
        } catch (e: Exception) {
            null
        }
    }

    fun getNetwork() : NodeAddressBook? {
        return try {
            getClient().let { client ->
                if (client != null) {
                    val addressBook = AddressBookQuery().setFileId(FileId.ADDRESS_BOOK).execute(client)
                    client.close()
                    addressBook
                } else {
                    return null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getSignedTransaction(transaction: Transaction<*>) : Transaction<*>{
        try {
            return transaction.sign(operatorKey())
        } catch (e: Exception) {
            throw e
        }
    }

    fun executeTransaction(transaction: Transaction<*>, isSigned: Boolean) : TransactionResponse? {
        try {
            getClient().let { client ->
                if (client != null) {
                    val response = if (isSigned) {
                        transaction.execute(client)
                    } else {
                        transaction.sign(operatorKey()).execute(client)
                    }
                    return response
                } else {
                    throw Exception("Error")
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

}

fun ByteArray.toBase64String(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}