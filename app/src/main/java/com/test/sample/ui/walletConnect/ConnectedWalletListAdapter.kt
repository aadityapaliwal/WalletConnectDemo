package com.test.sample.ui.walletConnect

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.sample.databinding.WalletConnectedItemviewBinding
import com.test.sample.modal.WalletConnectVO

interface ConnectedWalletListAdapterListener {
    fun onDisconnectSession(walletVO: WalletConnectVO)
}

class ConnectedWalletListAdapter(
    private var walletList: List<WalletConnectVO>,
    private val listener: ConnectedWalletListAdapterListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var binding: WalletConnectedItemviewBinding

    class WalletListViewHolder(binding: WalletConnectedItemviewBinding) : RecyclerView.ViewHolder(binding.root) {
        var accountId: TextView = binding.accountId
        val walletConnectedTime: TextView = binding.walletConnectedTime
        val disconnectWalletBtn: TextView = binding.disconnectBtn
        val sessionTitle: TextView = binding.sessionTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = WalletConnectedItemviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WalletListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return walletList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WalletListViewHolder) {
            with(holder) {
                val item = walletList[position]
                accountId.text = item.accountId
                walletConnectedTime.text = item.formatDate()
                sessionTitle.text = item.title

                disconnectWalletBtn.setOnClickListener {
                    listener?.onDisconnectSession(item)
                }
            }
        }
    }

}