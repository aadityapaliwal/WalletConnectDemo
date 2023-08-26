package com.test.sample.ui.walletConnect

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.walletconnect.web3.wallet.client.Wallet

enum class PairInfoStatus(status: String) {
    APPROVED("APPROVED"),
    REJECTED("REJECTED")
}

class PairProposalViewModel(
    val sessionProposal: Wallet.Model.SessionProposal,
    val verifyContext: Wallet.Model.VerifyContext
) : ViewModel() {

}