package com.test.sample.ui.walletConnect

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.sample.databinding.FragmentConnectedWalletBinding
import com.test.sample.modal.WalletConnectVO
import com.test.sample.ui.BaseFragment
import com.walletconnect.web3.wallet.client.Web3Wallet
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConnectedWalletFragment : BaseFragment<FragmentConnectedWalletBinding>() {

    private val viewModel: ConnectedWalletViewModel by viewModel()

    private val connectedWalletListAdapter by lazy {
        viewModel.connectedWalletList.value?.let {
            ConnectedWalletListAdapter(
                it,
                object : ConnectedWalletListAdapterListener {
                    override fun onDisconnectSession(walletVO: WalletConnectVO) {
                        viewModel.disconnectSession(walletVO)
                    }
                }
            )
        }
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentConnectedWalletBinding.inflate(inflater, container, false)

    override fun setupViewsAndObservers() {
        setHeaderAndFooter()
        viewModel.updateConnectedSessionList()

        activity?.let {
            binding.recyclerViewConnectedWalletItems.layoutManager =
                LinearLayoutManager(requireContext())
            binding.recyclerViewConnectedWalletItems.adapter = connectedWalletListAdapter
        }

        viewModel.connectedWalletList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                binding.recyclerViewConnectedWalletItems.visibility = View.VISIBLE
                binding.noActiveSessionAvailableTextview.visibility = View.GONE
            } else {
                binding.noActiveSessionAvailableTextview.visibility = View.VISIBLE
                binding.recyclerViewConnectedWalletItems.visibility = View.GONE
            }
        })

    }

    private fun setHeaderAndFooter() {
        binding.header1.title.setText("Connect Wallet")
        binding.header1.subTitle.setText("")

        binding.footer.buttonNext.visibility = View.GONE
        binding.footer.buttonBack.setOnClickListener {
            onBackPressed()
        }

        Log.d("Active sessions", Web3Wallet.getListOfActiveSessions().toString()) // do in background
    }

}