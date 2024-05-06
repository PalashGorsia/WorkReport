package com.app.workreport.ui.leave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.workreport.R
import com.app.workreport.databinding.CalendarFooterBinding
import com.app.workreport.util.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.util.Locale

class HolidayDetail(val date: LocalDate,val name:String) : BottomSheetDialogFragment() {
    private lateinit var binding: CalendarFooterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {
            binding = CalendarFooterBinding.inflate(inflater)
            handleViews()
        }
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun handleViews() {
        binding.apply {
            holidayDate.text = convertDateFormat(date.toString(), YYYY_MM_DD, DD)
            holidayMonth.text = convertDateFormat(date.toString(), YYYY_MM_DD, MMM)
            holidayDay.text = convertDateFormat(date.toString(), YYYY_MM_DD, EEEE)
           if (Locale.getDefault().language.equals(LANGUAGE_KEY_JAPANESE,true)){
               when(name){
                   LEAVE-> holidayName.text = requireContext().resources.getString(R.string.leave)
                   HOLIDAY -> holidayName.text = requireContext().resources.getString(R.string.holiday)
                   WEEKEND -> holidayName.text = requireContext().resources.getString(R.string.weekend)
               }
           }else{
               holidayName.text = name
           }




        }
        lifecycleScope.launchWhenCreated {
            delay(5000)
            dismiss()
        }
    }


    companion object {
        const val TAG = "HolidayDetail"

        fun newInstance(date: LocalDate,name:String) :HolidayDetail {
            return HolidayDetail(date,name)
        }
    }

}