package com.app.workreport.ui.otp

import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.app.workreport.R
import com.app.workreport.databinding.VerifyOtpFragmentBinding
import com.app.workreport.service.SmsBroadcastReceiver
import com.app.workreport.ui.dashboard.DashboardActivity
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.regex.Pattern

@AndroidEntryPoint
class VerifyOtpFragment : Fragment() {

    private  val viewModel: VerifyOtpViewModel by viewModels()
    private lateinit var binding: VerifyOtpFragmentBinding
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    var OTP = ""
    private val args:VerifyOtpFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = VerifyOtpFragmentBinding.inflate(inflater)
            handleView()
        }
        binding.lifecycleOwner = this
        binding.otpViewModel = viewModel
        handleObservers()
        return binding.root
    }

    private fun handleObservers() {
        viewModel.userData.observe(viewLifecycleOwner) {
            //  if (it){
            AppPref.isLogOut = true
            AppPref.isLoggedIn =false
            AppPref.accessToken = it.loginToken?.token
            navigateToDashboard()
            //   }
        }
        viewModel.userDataResentOtp.observe(viewLifecycleOwner) {
           // addOTPView(it.otpSent)
            startSmsUserConsent()
        }

        viewModel.err.observe(viewLifecycleOwner){
            if (it== ERROR_CODE){
                showErrorDialog(2)
                //binding.btnSend.showSnackBar(resources.getString(R.string.lost_sever_connection))
            }
        }
        viewModel.msg.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                requireActivity().infoDialogError(ERROR_MESSAGE_TYPE,it){
                    binding.firstPinView.setText("")
                }
               // showErrorDialog(ERROR_MESSAGE_TYPE,it)
                //binding.btnSend.showSnackBar(resources.getString(R.string.lost_sever_connection))
            }
        }

    }

    private fun addOTPView(otpSent: String?) {
     //   binding.firstPinView.setText(otpSent)

    }

    private fun navigateToDashboard() {
        AppPref.isLoggedIn = true
        requireActivity().xtnNavigate<DashboardActivity>()
        requireActivity().finishAffinity()

    }

    private fun handleView() {
        args.apply {
           // viewModel.userOtp =userOtp?:""
            viewModel.userNo =userNo?:""
            addOTPView(userOtp)
        }
        binding.apply {
            btnVerify.setOnClickListener {

                if (Objects.requireNonNull(firstPinView.text).toString()
                        .trim { it <= ' ' }.length == 4
                ) {
                    viewModel.userOtp =firstPinView.text.toString().trim()
                    if (requireContext().isNetworkAvailable())
                        viewModel.verifyOtp()
                    else
                        showErrorDialog(1)
                     //   btnVerify.showSnackBar(resources.getString(R.string.lost_internet))


                }else{
                    btnVerify.showSnackBar(resources.getString(R.string.enter_otp))
                }

            }
            btnResendOtp.setOnClickListener {
                if (requireContext().isNetworkAvailable())
                viewModel.resendOtp()
                else
                    showErrorDialog(1)
            }
            firstPinView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!!>=4){
                        //viewModel.isNoValid.postValue(true)
                        firstPinView.hideKeyboard()
                    }else{
                       // viewModel.isNoValid.postValue(false)
                    }
                }
            })

        }
    }
    private fun showErrorDialog(type: Int) {
        requireActivity().infoDialogError(type) {
            //  selectImage(pos, imageType, targetId)
        }

    }
    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver?.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent, REQ_USER_CONSENT)
                }

                override fun onFailure() {}
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
            requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
               // Timber.d("::%s", message)
                try {
                  //  val spilting = message?.split("account")?.toTypedArray()?.get(1)
                  //  xtnLog(spilting.toString(), TAGE)
                    OTP = message.toString()
                  //  Timber.d("::%s", spilting)
                 //   OTP = spilting?.split(" ")?.toTypedArray()?.get(1)?:""
                  //  OTP = OTP.split(" ").toTypedArray()[0]
                   // Timber.d("::%s", OTP)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                /* textViewMessage.setText(
                        String.format("%s - %s", getString(R.string.received_message), message));*/
                getOtpFromMessage(OTP)
            }
        }
    }

    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsUserConsent(null).addOnSuccessListener {
            xtnLog("On Success", TAGE)
            //                Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
        }.addOnFailureListener {
            xtnLog("On OnFailure", TAGE)
        }
    }

    private fun getOtpFromMessage(message: String) {
        try {
            val pattern = Pattern.compile("(|^)\\d{4}")
            val matcher = pattern.matcher(message)
            if (matcher.find()) {
                binding.firstPinView.setText(matcher.group(0))
            } else {
                binding.firstPinView.setText("")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    companion object {
        const val REQ_USER_CONSENT = 200
    }

    override fun onResume() {
        super.onResume()
        startSmsUserConsent()
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()

    }

    override fun onStop() {
        super.onStop()
            requireActivity().unregisterReceiver(smsBroadcastReceiver)
    }
}