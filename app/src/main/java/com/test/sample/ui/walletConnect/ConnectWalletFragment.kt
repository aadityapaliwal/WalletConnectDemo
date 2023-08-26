package com.test.sample.ui.walletConnect

import android.view.LayoutInflater
import android.view.ViewGroup
import com.test.sample.databinding.FragmentConnectWalletBinding
import com.test.sample.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class ConnectWalletFragment : BaseFragment<FragmentConnectWalletBinding>() {

    private val viewModel: ConnectWalletViewModel by viewModel()

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentConnectWalletBinding.inflate(inflater, container, false)

    override fun setupViewsAndObservers() {
        setHeaderAndFooter()
        viewModel.isParingComplete.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.isParingComplete.postValue(false)
                onBackPressed()
            }
        }
    }

    private fun setHeaderAndFooter() {
        binding.header1.title.setText("Pair Wallet")
        binding.header1.subTitle.setText("")

        binding.footer.apply {
            buttonNext.setText("Connect")
            buttonNext.setOnClickListener {
                validateURI().let {
                    val uriString = binding.uriStringEditText.editText?.text.toString()
                    viewModel.pairURI(uriString)
                }
            }

            buttonBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun validateURI() {
        binding.uriStringEditText.let {
            val text = it.editText?.text?.toString()?.trim()
            if (text.isNullOrEmpty()) {
                it.error = "Please enter URI String"
            } else {
                it.error = null
            }
        }
    }

}