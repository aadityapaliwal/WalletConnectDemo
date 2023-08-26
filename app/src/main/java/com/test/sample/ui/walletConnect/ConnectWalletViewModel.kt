package com.test.sample.ui.walletConnect

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.sample.common.AppLog
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectWalletViewModel(

) : ViewModel() {

    val isParingComplete = MutableLiveData<Boolean>(false)

    fun pairURI(uriString: String) {
        viewModelScope.executeAsyncTask(
            onPreExecute = {
            }, doInBackground = {
                try {
                    val pairingParams = Wallet.Params.Pair(uriString)
                    Web3Wallet.pair(
                        params = pairingParams,
                        onSuccess = { pair ->
                            AppLog.d("Success in Paring", pair.uri)
                            Handler(Looper.getMainLooper()).post {
                                isParingComplete.postValue(true)
                            }
                        },
                        onError = { error ->
                            AppLog.e("Error in Paring", error.toString())
                            Handler(Looper.getMainLooper()).post {
                            }
                        },
                    )
                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).post {
                    }
                }
            }, onPostExecute = {
            }
        )
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