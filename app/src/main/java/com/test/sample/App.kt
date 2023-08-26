package com.test.sample

import android.app.Application
import com.test.sample.common.AppLog
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

	init {
		instance = this
	}

	override fun onCreate() {
		super.onCreate()

		// Start Koin
		startKoin {
			androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
			androidContext(this@App)
			modules(appModule)
		}

		setUpWalletConnect()
	}

	private fun setUpWalletConnect() {
		val projectId = "8d3eb7a07448fa3976d8d6ad9b8f0f18"
		val relayUrl = "relay.walletconnect.com"
		val serverUrl = "wss://$relayUrl?projectId=$projectId"
		val connectionType = ConnectionType.AUTOMATIC //or ConnectionType.MANUAL
		val redirect = null
		val appMetaData = Core.Model.AppMetaData(
			name = "Sample App",
			description = "Wallet Description",
			url = "Wallet URL",
			icons = listOf(""),  /*list of icon url strings*/
			redirect= null
		)

		val errorCallback: (Core.Model.Error) -> Unit = { error ->
			AppLog.e("CoreClient initialize Error", error.toString())
		}

		CoreClient.initialize(relayServerUrl = serverUrl, connectionType = connectionType, application = this, metaData = appMetaData, onError = errorCallback)

		val initParams = Wallet.Params.Init(CoreClient)
		Web3Wallet.initialize(initParams, onSuccess = {
			AppLog.d("Web3Wallet Initialize Successfully","Successfully")
		}, onError = {error ->
			AppLog.e("Web3Wallet Initialize Error", error.toString())
		})
	}

	companion object {
		lateinit var instance: App
			private set
	}
}