package com.test.sample.ui

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.protobuf.InvalidProtocolBufferException
import com.hedera.hashgraph.sdk.Transaction
import com.hedera.hashgraph.sdk.proto.Query
import com.hedera.hashgraph.sdk.proto.SignedTransaction
import com.hedera.hashgraph.sdk.proto.TransactionBody
import com.test.sample.common.HederaHashgraphManager
import com.test.sample.HomeNavGraphDirections
import com.test.sample.R
import com.test.sample.common.AppLog
import com.test.sample.common.toBase64String
import com.test.sample.databinding.ActivityMainBinding
import com.test.sample.modal.SignTransactionModel
import com.test.sample.modal.WCTransactionModel
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.util.encoders.Hex
import org.json.JSONObject

class MainActivity : AppCompatActivity(), LifecycleObserver {

	private lateinit var binding: ActivityMainBinding

	val mainNavController: NavController by lazy { findNavController(R.id.nav_host_fragment) }
	var currentDrawerItemId: Int? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		super.onCreate(savedInstanceState)

		lifecycle.addObserver(this)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}

	fun hasUserSignatureExists(bytes: ByteArray) : Boolean {
		var hasSign = false
		try {
			val transaction = com.hedera.hashgraph.sdk.proto.Transaction.parseFrom(bytes)
			val userPublicKeyHex = getString(R.string.operator_public_key)
			val signedTransaction = SignedTransaction.parseFrom(transaction.signedTransactionBytes)
			for (item in signedTransaction.sigMap.sigPairList) {
				val pubKeyHex = Hex.toHexString(item.pubKeyPrefix.toByteArray())
				AppLog.d("Determined pubKeyHex", pubKeyHex)
				AppLog.d("User pubKeyHex", userPublicKeyHex ?: "")
				if (userPublicKeyHex == pubKeyHex) {
					hasSign = true
					break
				}
			}
		}catch (e: Exception) {
			AppLog.e("hasUserSignatureExists", e.toString())
		}

		return hasSign
	}

	override fun onStart() {
		super.onStart()

		val walletDelegate = object : Web3Wallet.WalletDelegate {
			override fun onSessionProposal(sessionProposal: Wallet.Model.SessionProposal, verifyContext: Wallet.Model.VerifyContext) {
				AppLog.d("onSessionProposal", sessionProposal.name)
				runOnUiThread {
					AppLog.d("Calling in Main Thread", sessionProposal.name)
					val proposalStr = Gson().toJson(sessionProposal)
					val contextStr = Gson().toJson(verifyContext)
					handlePairProposalRequest(proposalStr, contextStr)
				}
				// Triggered when wallet receives the session proposal sent by a Dapp
			}

			override fun onSessionRequest(sessionRequest: Wallet.Model.SessionRequest, verifyContext: Wallet.Model.VerifyContext) {
				// Triggered when a Dapp sends SessionRequest to sign a transaction or a message
				AppLog.d("onSessionRequest", "topic " + sessionRequest.topic + " request " + sessionRequest.request.toString())
				when (sessionRequest.request.method) {
					"getMirrorNetwork" -> {
						handleGetMirrorNetworkRequest(sessionRequest)
					}
					"getNetwork" -> {
						handleGetNetworkRequest(sessionRequest)
					}
					"getAccountBalance" -> {
						handleGetAccountBalanceRequest(sessionRequest)
					}
					"call"-> {
						sessionRequest.request.params.let {
							if (it.isNotEmpty()) {
								val wcTransaction = Gson().fromJson(it, WCTransactionModel::class.java)
								if (wcTransaction.isTransaction == true) {
									val txnBytes = wcTransaction.executable.base64ToByteArray()
									if (hasUserSignatureExists(txnBytes)) { //As user signature available, execute it
										handleAlreadySignedRequest(sessionRequest, txnBytes)
									} else { //Show the UI and get confirmation
										val signData = SignTransactionModel(txnBytes, sessionRequest)
										//handleSignTransactionRequest(signData)
									}
								} else {
									handleQueryRequest(sessionRequest, wcTransaction)
								}
							} else {
								AppLog.d("sessionRequest", "param is null")
							}
						}
					}
					"signTransaction" -> {
						sessionRequest.request.params.let {
							if (!it.isNullOrEmpty()) {
								val wcTransaction = Gson().fromJson(it, WCTransactionModel::class.java)
								val txnBytes = wcTransaction.executable.base64ToByteArray()
								val signData = SignTransactionModel(txnBytes, sessionRequest)
								//handleSignTransactionRequest(signData)
							} else {
								AppLog.d("signTransaction", "request.params is null or not correct format")
							}
						}
					}
				}

			}

			override fun onAuthRequest(authRequest: Wallet.Model.AuthRequest, verifyContext: Wallet.Model.VerifyContext) {
				AppLog.d("onAuthRequest", authRequest.pairingTopic)
				// Triggered when Dapp / Requester makes an authorization request
			}

			override fun onSessionDelete(sessionDelete: Wallet.Model.SessionDelete) {
				AppLog.d("onSessionDelete", "called")
				// Triggered when the session is deleted by the peer
			}

			override fun onSessionSettleResponse(settleSessionResponse: Wallet.Model.SettledSessionResponse) {
				AppLog.d("onSessionSettleResponse", "called")
				// Triggered when wallet receives the session settlement response from Dapp
			}

			override fun onSessionUpdateResponse(sessionUpdateResponse: Wallet.Model.SessionUpdateResponse) {
				AppLog.d("onSessionUpdateResponse", "called")
				// Triggered when wallet receives the session update response from Dapp
			}

			override fun onConnectionStateChange(state: Wallet.Model.ConnectionState) {
				AppLog.d("onConnectionStateChange", state.isAvailable.toString())
				//Triggered whenever the connection state is changed
			}

			override fun onError(error: Wallet.Model.Error) {
				AppLog.d("WalletDelegate onError", error.toString())
				// Triggered whenever there is an issue inside the SDK
			}
		}

		Web3Wallet.setWalletDelegate(walletDelegate)
	}

	private fun handleGetMirrorNetworkRequest(sessionRequest: Wallet.Model.SessionRequest) {
		val mirrorNetworks = HederaHashgraphManager.getMirrorNetwork()
		val resultData = JSONObject().put("mirrornetwork", mirrorNetworks)
		val respond : Wallet.Params.SessionRequestResponse = if (mirrorNetworks.isNullOrEmpty()) {
			val error = Wallet.Model.JsonRpcResponse.JsonRpcError(sessionRequest.request.id, -1, "Not found")
			Wallet.Params.SessionRequestResponse(sessionRequest.topic, error)
		} else {
			val result = Wallet.Model.JsonRpcResponse.JsonRpcResult(sessionRequest.request.id, resultData.toString())
			Wallet.Params.SessionRequestResponse(sessionRequest.topic, result)
		}

		AppLog.d("Responding Request", "handleGetMirrorNetworkRequest: Response $respond")
		Web3Wallet.respondSessionRequest(respond,
			onSuccess = {
				AppLog.d("OnSessionRequest Success", "handleGetMirrorNetworkRequest: Completed")
			},
			onError = {error ->
				AppLog.d("OnSessionRequest Error", "handleGetMirrorNetworkRequest $error")
			})
	}
	private fun handleGetNetworkRequest(sessionRequest: Wallet.Model.SessionRequest) {
		val nodeAddress = arrayListOf<String>()

		val networkRes = HederaHashgraphManager.getNetwork()
		val respond : Wallet.Params.SessionRequestResponse = if (networkRes == null || networkRes.nodeAddresses.isNullOrEmpty() || networkRes.nodeAddresses?.first() == null) {
			val error = Wallet.Model.JsonRpcResponse.JsonRpcError(sessionRequest.request.id, -1, "No address found")
			Wallet.Params.SessionRequestResponse(sessionRequest.topic, error)
		} else {
			val accountID = networkRes.nodeAddresses?.first()?.accountId
			nodeAddress.add("${accountID?.realm}.${accountID?.shard}.${accountID?.num}")
			val result = Wallet.Model.JsonRpcResponse.JsonRpcResult(sessionRequest.request.id, nodeAddress.toString())
			Wallet.Params.SessionRequestResponse(sessionRequest.topic, result)
		}

		AppLog.d("Responding Request", "handleGetNetworkRequest: Response $respond")
		Web3Wallet.respondSessionRequest(respond,
			onSuccess = {
				AppLog.d("OnSessionRequest Success", "handleGetNetworkRequest: Completed")
			},
			onError = {error ->
				AppLog.d("OnSessionRequest Error", "handleGetNetworkRequest $error")
			})
	}

	private fun handleGetAccountBalanceRequest(sessionRequest: Wallet.Model.SessionRequest) {
		HederaHashgraphManager.getAccountBalance(onSuccess = { it, errorMsg ->
			val respond = if (it == null) {
				val result = Wallet.Model.JsonRpcResponse.JsonRpcError(sessionRequest.request.id, -1, errorMsg ?: "Query Result is empty")
				Wallet.Params.SessionRequestResponse(sessionRequest.topic, result)
			} else {
				val jsonResult = JSONObject().put("hbars", it.hbars)
				val result = Wallet.Model.JsonRpcResponse.JsonRpcResult(sessionRequest.request.id, jsonResult.toString())
				Wallet.Params.SessionRequestResponse(sessionRequest.topic, result)
			}

			AppLog.d("Responding Request", "handleGetAccountBalanceRequest: Response $respond")
			Web3Wallet.respondSessionRequest(respond,
				onSuccess = {
					AppLog.d("OnSessionRequest Success", "handleGetAccountBalanceRequest: Completed")
				},
				onError = {error ->
					AppLog.d("OnSessionRequest Error", "handleGetAccountBalanceRequest $error")
				})
		})
	}

	private fun handleQueryRequest(sessionRequest: Wallet.Model.SessionRequest, wcTransaction: WCTransactionModel) {
		val txnBytes = wcTransaction.executable.base64ToByteArray()
		val query = Query.parseFrom(txnBytes)
		AppLog.d("Query data", query.toFormattedString())

		when (query.queryCase) {
			Query.QueryCase.CRYPTOGETINFO -> {
				HederaHashgraphManager.getAccountInfo(query.cryptoGetInfo.accountID.toByteArray(), onSuccess = { result, error ->
					sendQueryResponse(sessionRequest, result, error)
				})
			}
			Query.QueryCase.TRANSACTIONGETRECEIPT -> {
				HederaHashgraphManager.getTransactionReceiptInfo(query.transactionGetReceipt.transactionID.toByteArray(), onSuccess = { result, error ->
					sendQueryResponse(sessionRequest, result, error)
				})
			}
			Query.QueryCase.CRYPTOGETACCOUNTBALANCE -> {
				HederaHashgraphManager.getAccountBalance(onSuccess = { it, errorMsg ->
					sendQueryResponse(sessionRequest, it?.toBytes()?.toByteArray()?.toBase64String(), errorMsg)
				})
			}
			else -> {
				AppLog.d("sessionRequest", "Query not supported")
				null
			}
		}
	}

	private fun sendQueryResponse(sessionRequest: Wallet.Model.SessionRequest, queryResult: String?, errorMsg: String?) {
		val respond = if (queryResult.isNullOrEmpty()) {
			val result = Wallet.Model.JsonRpcResponse.JsonRpcError(sessionRequest.request.id, -1, errorMsg ?: "Query Result is empty or not supported")
			Wallet.Params.SessionRequestResponse(sessionRequest.topic, result)
		} else {
			val result = Wallet.Model.JsonRpcResponse.JsonRpcResult(sessionRequest.request.id, queryResult)
			Wallet.Params.SessionRequestResponse(sessionRequest.topic, result)
		}

		AppLog.d("Responding Request", "sendQueryResponse: Response $respond")
		Web3Wallet.respondSessionRequest(respond,
			onSuccess = {
				AppLog.d("OnSessionRequest Success", "handleQueryRequest: Completed")
			},
			onError = {error ->
				AppLog.d("OnSessionRequest Error", "handleQueryRequest $error")
			})
	}

	private fun handleAlreadySignedRequest(sessionRequest: Wallet.Model.SessionRequest, byteArray: ByteArray) {
		val transaction = Transaction.fromBytes(byteArray)
		val result = Wallet.Model.JsonRpcResponse.JsonRpcResult(
			sessionRequest.request.id,
			transaction.toString()
		)
		val respond = Wallet.Params.SessionRequestResponse(sessionRequest.topic, result)
		Web3Wallet.respondSessionRequest(respond,
			onSuccess = {
				AppLog.d("OnSessionRequest Success", "handleAlreadySignedRequest: Completed")
			},
			onError = { error ->
				AppLog.d("OnSessionRequest Error", "handleAlreadySignedRequest $error")
			})
	}


	private fun handlePairProposalRequest(proposal: String, contextStr: String) {
		mainNavController.popBackStack(R.id.pairProposalFragment, false)
		val action = HomeNavGraphDirections.actionGlobalPairProposalFragment(proposal, contextStr)
		navigate(action, null)
	}


	private fun navigate(action: NavDirections, navOptions: NavOptions?) {
		with(mainNavController) {
			currentDestination?.getAction(action.actionId)?.let {
				navigate(action, navOptions)
			}
		}
	}

	private fun handleDrawerItemSelection() {
		var args: Bundle? = null
		currentDrawerItemId?.let { mainNavController.navigate(it, args, getDefaultNavOptions()) }
		currentDrawerItemId = null
	}

	private fun Query.toFormattedString(): String {

		val payment = when {
			cryptoGetAccountRecords.hasHeader() -> cryptoGetAccountRecords.header.payment
			fileGetContents.hasHeader() -> fileGetContents.header.payment
			cryptoGetInfo.hasHeader() -> cryptoGetInfo.header.payment
			else -> cryptogetAccountBalance.header.payment
		}

		return this.toString() + "\n paymentBody: ${payment.getTxnBody()}"
	}

	fun com.hedera.hashgraph.sdk.proto.Transaction.getTxnBody(): TransactionBody {
		val bodyBytes = bodyBytes
		if (bodyBytes != null && bodyBytes.size() > 0) {
			try {
				return TransactionBody.parseFrom(bodyBytes)
			} catch (e: InvalidProtocolBufferException) {
				e.printStackTrace()
			}

		}
		return body
	}


	fun String.base64ToByteArray(): ByteArray {
		return Base64.decode(this, Base64.NO_WRAP)
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

	fun getDefaultNavOptions(
		enabledEnterAnim: Boolean = true,
		enablePopAnim: Boolean = true,
		popUpTo: Int? = null,
		popInclusive: Boolean = false
	): NavOptions? {
		return NavOptions.Builder().apply {
			if (enabledEnterAnim) {
				//setEnterAnim(R.anim.fragment_open_enter)
				//setExitAnim(R.anim.fragment_open_exit)
			}

			if (enablePopAnim) {
				//setPopEnterAnim(R.anim.fragment_close_enter)
				//setPopExitAnim(R.anim.fragment_close_exit)
			}

			popUpTo?.let {
				setPopUpTo(it, popInclusive)
			}

		}.build()
	}
}
