package com.test.sample.ui.walletConnect

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.sample.modal.WalletConnectVO
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectedWalletViewModel(
) : ViewModel() {

    var connectedWalletList = MutableLiveData<List<WalletConnectVO>>()

    fun disconnectSession(walletVO: WalletConnectVO) {
        viewModelScope.executeAsyncTask(
            onPreExecute = {
            }, doInBackground = {
                try {
                    val sessionTopic: String = walletVO.sessionTopic
                    val disconnectParams = Wallet.Params.SessionDisconnect(sessionTopic)

                    Web3Wallet.disconnectSession(params = disconnectParams,
                        onSuccess = {
                            Handler(Looper.getMainLooper()).post {
                                updateConnectedSessionList()
                            }
                        },
                        onError = { error ->
                            Handler(Looper.getMainLooper()).post {

                            }
                        }
                    )
                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).post {

                    }
                }
            }, onPostExecute = {
            }
        )

    }

    fun updateConnectedSessionList() {
        val walletList = ArrayList<WalletConnectVO>()
        Web3Wallet.getListOfActiveSessions().let {
            it.forEach {
                it.metaData?.name?.let { title ->
                    val connectedVO = WalletConnectVO(
                        it.namespaces["hedera"]?.accounts?.get(0),
                        it.expiry,
                        title,
                        it.topic
                    )
                    walletList.add(connectedVO)
                }
            }
        }
        connectedWalletList.value = walletList
    }

    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch {
        onPreExecute() // runs in Main Thread
        val result = withContext(Dispatchers.IO) {
            doInBackground() // runs in background thread without blocking the Main Thread
        }
        onPostExecute(result) // runs in Main Thread
    }

}