package com.test.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding> : Fragment(){

	private var _binding: T? = null
	protected val binding get() = _binding!!

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = this.setBinding(inflater, container)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
			onBackPressed()
		}

		setupViewsAndObservers()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	protected open fun allowBack() = true
	protected open fun onBackPressed() {
		if (allowBack()) {
			if (!findNavController().popBackStack()) {
				activity?.finish()
			}
		}
	}

	protected open fun onBackPressed(destinationId: Int, inclusive: Boolean) {
		if (allowBack()) {
			findNavController().popBackStack(destinationId, inclusive)
		}
	}

	protected open fun navigateTo(directions: NavDirections, navOptions: NavOptions? = getDefaultNavOptions()) {
		findNavController().navigate(directions, navOptions)
	}

	abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): T
	abstract fun setupViewsAndObservers()

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

