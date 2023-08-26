package com.test.sample.ui.walletConnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.sample.HomeNavGraphDirections
import com.test.sample.databinding.FragmentWalletConnectMainmenuBinding
import com.test.sample.ui.BaseFragment


class WalletConnectMenuFragment : BaseFragment<FragmentWalletConnectMainmenuBinding>() {
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentWalletConnectMainmenuBinding.inflate(inflater, container, false)

    override fun setupViewsAndObservers() {
        setHeaderAndFooter()

        binding.layoutConnectWallet.apply {
            img.visibility = View.GONE
            textTitle.text = "Connect Dapp"
            this.root.setOnClickListener {
                val action = HomeNavGraphDirections.actionWalletConnectMenuFragmentToConnectWalletFragment()
                navigateTo(action)
            }
        }

        binding.layoutConnectedWallet.apply {
            img.visibility = View.GONE
            textTitle.text = "Connected Dapps"
            this.root.setOnClickListener {
                val action = HomeNavGraphDirections.actionWalletConnectMenuFragmentToConnectedWalletFragment()
                navigateTo(action)
            }
        }

    }

    private fun setHeaderAndFooter() {
        binding.footer.buttonNext.visibility = View.GONE
        binding.footer.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

}