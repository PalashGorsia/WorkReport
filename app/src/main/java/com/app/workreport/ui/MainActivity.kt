package com.app.workreport.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import com.app.workreport.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import com.app.workreport.R
import com.app.workreport.ui.dashboard.DashboardActivity
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        xtnSetLanguage(Locale.getDefault().language)
     //   xtnSetLanguage(LANGUAGE_KEY_JAPANESE)
        AppPref.local = if (Locale.getDefault().language.equals(
                LANGUAGE_KEY_JAPANESE,
                true
            ) || Locale.getDefault().language.equals(LANGUAGE_KEY_WEB_JAPANESE, true)
        ) LANGUAGE_KEY_WEB_JAPANESE else LANGUAGE_KEY_WEB_ENGLISH

    }


    override fun onResume() {
        super.onResume()
     //   AppPref.isLoggedIn = false
        xtnLog(AppPref.isLoggedIn.toString(), TAGE)
        if (AppPref.isLoggedIn){
            navigateToDashboard()
        }else{
            navController = findNavController(R.id.nav_pre_login)
        }

    }

    private fun navigateToDashboard() {
       xtnNavigate<DashboardActivity>()
        finishAffinity()
    }

}