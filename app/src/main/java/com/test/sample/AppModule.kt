package com.test.sample

import com.test.sample.ui.walletConnect.ConnectWalletViewModel
import com.test.sample.ui.walletConnect.ConnectedWalletViewModel
import com.test.sample.ui.walletConnect.PairProposalViewModel
import com.walletconnect.web3.wallet.client.Wallet
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

	viewModel { ConnectWalletViewModel() }
	viewModel { (proposal: Wallet.Model.SessionProposal, verifyContext: Wallet.Model.VerifyContext) -> PairProposalViewModel(proposal, verifyContext) }
	viewModel { ConnectedWalletViewModel() }
}