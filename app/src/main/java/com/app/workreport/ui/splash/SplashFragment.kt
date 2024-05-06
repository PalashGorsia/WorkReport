package com.app.workreport.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.workreport.R
import com.app.workreport.databinding.SplashFragmentBinding
import com.app.workreport.util.AppPref
import com.app.workreport.util.xtnRunDelayed

class SplashFragment : Fragment() {

    private lateinit var binding:SplashFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = SplashFragmentBinding.inflate(inflater)
        }
        binding.lifecycleOwner =this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        if (AppPref.isLogOut){
            findNavController().navigate(R.id.actionSplashToLogin)
            AppPref.isLogOut =false
        }else{
            try {
                if (!AppPref.isLoggedIn){
                    xtnRunDelayed({ findNavController().navigate(R.id.actionSplashToLogin) }, 1000)
                }else{
//
                }
            }catch (e:Exception){

        }
        }
    }
/*
    private fun navigateToDashboard() {
        requireActivity().xtnNavigate<DashboardActivity>()
        requireActivity().finishAffinity()
    }
*/

}