package com.app.workreport.ui.login

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.workreport.R
import com.app.workreport.databinding.LoginFragmentBinding
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private  val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = LoginFragmentBinding.inflate(inflater)
            handleView()
        }
        binding.lifecycleOwner =this
        binding.viewModelLoin = viewModel
        handleObservers()
        return binding.root
    }

    private fun handleObservers() {
        viewModel.userData.observe(viewLifecycleOwner) {
              //  AppPref.local = "EN"
            AppPref.userID = it.id
                val action =
                    LoginFragmentDirections.actionLoginToVerifyOtp("", viewModel.userNo.value)
                findNavController().navigate(action)
        }
        viewModel.isNoValid.observe(viewLifecycleOwner){
            if (it) binding.mobileNo.hideKeyboard()
        }

        viewModel.msg.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                when {
                    it.contains("buffering timed out",true) -> showErrorDialog(2)
                    it.equals(NO_NO) -> binding.btnSend.showSnackBar(resources.getString(R.string.empty_number))
                    it.equals(VALID_NO) -> binding.btnSend.showSnackBar(resources.getString(R.string.valid_phone_number))
                    else -> showErrorDialog(ERROR_MESSAGE_TYPE,it)
                }
            }
        }
        viewModel.err.observe(viewLifecycleOwner){
            if (it== ERROR_CODE){
                showErrorDialog(2)
            }
        }
    }

    private fun showErrorDialog(type: Int,message:String="") {
        requireActivity().infoDialogError(type,message) {
        }

    }

    private fun handleView() {
        binding.apply {
            val displayMetrics = DisplayMetrics()
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)

            var width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            val param = logo.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,height*5/100,0,0)
            logo.layoutParams = param

          /*  val paramInfo = infoNo.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(8,height*15/100,8,0)
            infoNo.layoutParams = paramInfo*/

            AppPref.deviceToken ="abc234"
            btnSend.setOnClickListener {
                if (requireContext().isNetworkAvailable()){
                    viewModel.login()
                }else{
                    showErrorDialog(1)
                }

            }
        }

    }

}