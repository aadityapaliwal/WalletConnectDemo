package com.test.sample.ui.walletConnect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.gson.GsonBuilder
import com.test.sample.R
import com.test.sample.common.AppLog
import com.test.sample.databinding.FragmentShareWalletInfoBinding
import com.test.sample.ui.BaseFragment
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PairProposalFragment : BaseFragment<FragmentShareWalletInfoBinding>() {

    private val args: PairProposalFragmentArgs by navArgs()
    private val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
    private val viewModel: PairProposalViewModel by viewModel() { parametersOf(gsonBuilder.fromJson(args.proposal, Wallet.Model.SessionProposal::class.java), gsonBuilder.fromJson(args.verifyContext, Wallet.Model.VerifyContext::class.java)) }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentShareWalletInfoBinding.inflate(inflater, container, false)

    override fun setupViewsAndObservers() {
        setUpHeaderAndFooter()

        binding.accIdTextView.text = ""
        viewModel.sessionProposal.let {
            binding.qaPublisherLinkTextView.text = it.url
        }
    }

    private fun setUpHeaderAndFooter() {
        binding.header1.apply {
            this.title.setText("Pair Wallet")
            this.subTitle.setText("")
        }

        binding.footer.apply {
            this.buttonNext.text = "Approve"
            this.buttonNext.setOnClickListener {
                storeUserResponse(PairInfoStatus.APPROVED.name)
            }
            this.buttonBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        storeUserResponse(PairInfoStatus.REJECTED.name)
    }

    private fun storeUserResponse(status: String) {
        try {
            if (status == PairInfoStatus.APPROVED.name) {
                val sessionApprove = getNameSpace()
                sessionApprove.let {
                    Web3Wallet.approveSession(params = sessionApprove,
                        onSuccess = {
                            requireActivity().runOnUiThread {
                                AppLog.d("PairProposalFragment", "Approve Success")
                                super.onBackPressed()
                            }
                        },
                        onError = {error ->
                            requireActivity().runOnUiThread {
                                AppLog.d("PairProposalFragment", "Approve Error $it")
                                super.onBackPressed()
                            }
                        })
                }
            } else {
                val rejectSession = Wallet.Params.SessionReject(proposerPublicKey = viewModel.sessionProposal.proposerPublicKey, "User Rejected")
                Web3Wallet.rejectSession(rejectSession,
                    onSuccess = {
                        AppLog.d("PairProposalFragment", "Reject Success")
                        requireActivity().runOnUiThread {
                            super.onBackPressed()
                        }
                    },
                    onError = {
                        AppLog.d("PairProposalFragment", "Reject Error $it")
                        super.onBackPressed()
                    })
            }
        }catch (e: Exception) {

        }
    }

    @Throws
    private fun getNameSpace(): Wallet.Params.SessionApprove {
        if (viewModel.sessionProposal.requiredNamespaces.count() > 1) {
            throw Exception("Multiple chain not supported")
        }

        viewModel.sessionProposal.requiredNamespaces.let {
            if (it.isEmpty()) {
                throw Exception("No chain for approval")
            }

            if (it.entries.first().key.lowercase() != "hedera") {
                throw Exception(it.entries.first().key + " Chain not supported")
            }

            val proposalNameSpace = it.values
            val blockChains = proposalNameSpace.first().chains

            if (blockChains.isNullOrEmpty()) {
                throw Exception("No chain available to subscribe")
            }

            if (blockChains.size > 1) {
                throw Exception("Multiple blockchain not supported")
            }

            val proposerPublicKey: String = viewModel.sessionProposal.proposerPublicKey /*Proposer publicKey from SessionProposal object*/
            val namespace: String = it.entries.first().key /*Namespace identifier, see for reference: https://github.com/ChainAgnostic/CAIPs/blob/master/CAIPs/caip-2.md#syntax*/
            val accounts: List<String> = listOf(proposalNameSpace.first().chains!!.first() + ":" + getString(
                R.string.operator_acc_id)) /*List of accounts on chains*/
            val methods: List<String> = proposalNameSpace.first().methods /*List of methods that wallet approves*/
            val events: List<String> = proposalNameSpace.first().events/*List of events that wallet approves*/

            val namespaces: Map<String, Wallet.Model.Namespace.Session> = mapOf(Pair(namespace, Wallet.Model.Namespace.Session(chains = blockChains, accounts = accounts, methods = methods,events = events)))

            val approveParams: Wallet.Params.SessionApprove = Wallet.Params.SessionApprove(proposerPublicKey, namespaces)
            return approveParams
        }
    }

}