package com.app.workreport.ui.leave

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.app.workreport.R
import com.app.workreport.databinding.CalendarDayBinding
import com.app.workreport.databinding.FragmentAddLeaveBinding
import com.app.workreport.model.HolidayData
import com.app.workreport.model.HolidaysList
import com.app.workreport.model.LeaveRequest
import com.app.workreport.ui.customCalendar.*
import com.app.workreport.ui.customCalendar.ContinuousSelectionHelper.getSelection
import com.app.workreport.ui.customCalendar.ContinuousSelectionHelper.isInDateBetweenSelection
import com.app.workreport.ui.customCalendar.ContinuousSelectionHelper.isOutDateBetweenSelection
import com.app.workreport.ui.customCalendar.model.CalendarDay
import com.app.workreport.ui.customCalendar.model.CalendarMonth
import com.app.workreport.ui.customCalendar.model.DayPosition
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth


@AndroidEntryPoint
class AddLeaveFragment : Fragment() {
    private  val viewModel: LeaveRequestViewModel by activityViewModels()
    lateinit var binding:FragmentAddLeaveBinding
    private val today = LocalDate.now()

    private var selection = DateSelection()
    private var leaveList:ArrayList<String> = ArrayList()
    private var holidayList : ArrayList<HolidayData> = ArrayList()
    private var weekEndList : ArrayList<String> = ArrayList()
    private var workingDayList : ArrayList<String> = ArrayList()


    companion object {
        fun newInstance() = AddLeaveFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = FragmentAddLeaveBinding.inflate(inflater)

        }
        binding.lifecycleOwner = this
        handleView()
        handleObservers()
        return binding.root
        
    }

    private fun handleObservers() {
        viewModel.responseLeave.observe(viewLifecycleOwner){
            binding.progressBar.isVisible = false
            selection.startDate = null
            selection.endDate = null
            /*if (it)
                findNavController().popBackStack()*/
        }

        viewModel.responseHolidays.observe(viewLifecycleOwner){
            binding.progressBar.isVisible = false
          //  requireContext().xtnToast(it[0]?.date?:"")
            configureBinders(it)
            //2023-03-18T00:00:00.000Z
        }
        viewModel.resMessAddLeave.observe(viewLifecycleOwner){
            if (it.isNotEmpty())
                binding.btnApplyLeave.showSnackBar(it)
        }

    }
    private fun handleView() {
        binding.progressBar.isVisible = true
        viewModel.getHolidays()


        binding.btnApplyLeave.setOnClickListener click@{
            val (startDate, endDate) = selection
            if (startDate != null && endDate != null) {
                AppPref.local
                val formatter = SimpleDateFormat(YYYY_MM_DD)
                binding.progressBar.isVisible = true
                val leaveList = formatter.parse(selection.startDate.toString())
                    ?.let { it1 -> formatter.parse(selection.endDate.toString())
                        ?.let { it2 -> getDatesBetween(it1, it2,leaveList,holidayList,weekEndList) } }
                viewModel.addWorkerLeave(LeaveRequest(getLocale(),AppPref.userID?:"",leaveList!!))
            }else if (startDate != null ){
                leaveList.clear()
                binding.progressBar.isVisible = true
                leaveList.add(convertDateFormat(startDate.toString(), YYYY_MM_DD, YYYY_MM_DD_T_HH_MM_SS_24))
                viewModel.addWorkerLeave(LeaveRequest(getLocale(),AppPref.userID?:"",leaveList))
            }
        }
        bindSummaryViews()
    }

    private fun configureBinders(holidaysLists: List<HolidaysList>) {
        weekEndList.clear()
        leaveList.clear()
        holidayList.clear()
        for (i in holidaysLists.indices){
            when(holidaysLists[i].type){
                WEEKEND ->{
                    weekEndList.add(convertDateFormat(holidaysLists[i].date,
                        YYYY_MM_DD_T_HH_MM_SS_24, YYYY_MM_DD))
                }
                LEAVE ->{
                    leaveList.add(convertDateFormat(holidaysLists[i].date,
                        YYYY_MM_DD_T_HH_MM_SS_24, YYYY_MM_DD))
                }
                JOB_TYPE ->{
                    workingDayList.add(convertDateFormat(holidaysLists[i].date,
                        YYYY_MM_DD_T_HH_MM_SS_24, YYYY_MM_DD))
                }
                HOLIDAY ->{
                    holidayList.add(HolidayData(convertDateFormat(holidaysLists[i].date,
                        YYYY_MM_DD_T_HH_MM_SS_24, YYYY_MM_DD),
                        holidaysLists[i].holidayId?.name?:"",
                    holidaysLists[i].holidayId?._id?:"")  )
                }
            }
        }

        val daysOfWeek = daysOfWeek()

        val currentMonth = YearMonth.now()
        binding.exFourCalendar.setup(
            currentMonth,
            currentMonth.plusMonths(2),
            daysOfWeek.first(),
        )
        binding.exFourCalendar.scrollToMonth(currentMonth)

        val clipLevelHalf = 5000
        val ctx = requireContext()

        val rangeStartBackground =
            ctx.getDrawableCompat(R.drawable.selected_date_bg_start).also {
                it.level = clipLevelHalf // Used by ClipDrawable
            }
        val rangeEndBackground =
            ctx.getDrawableCompat(R.drawable.selected_date_bg_end).also {
                it.level = clipLevelHalf // Used by ClipDrawable
            }
        val rangeMiddleBackground =
            ctx.getDrawableCompat(R.drawable.selected_bg_date_middle)
        val singleBackground = ctx.getDrawableCompat(R.drawable.date_single_selected_bg)
        val holidayBackground = ctx.getDrawableCompat(R.drawable.ellipse_red)
        val weekEndBackground = ctx.getDrawableCompat(R.drawable.weekend_background)
        val todayBackground = ctx.getDrawableCompat(R.drawable.date_today_bg)
        val leaveBackground = ctx.getDrawableCompat(R.drawable.ellipse_yellow)
        val workingDay = ctx.getDrawableCompat(R.drawable.ellipse_green)



/*Leave - Yellow is OK, National holiday & Sunday :It is better for us to be Red both. , Saturday: It is also better for us to be sky-blue.*/



        //call function first time
        binding.exFourCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.bind.exFourDayText
                val roundBgView = container.bind.exFourRoundBackgroundView
                val continuousBgView = container.bind.exFourContinuousBackgroundView


                textView.text = null
                roundBgView.makeInVisible()
                continuousBgView.makeInVisible()
               // layToolTip.makeGone()

                val (startDate, endDate) = selection

                when (data.position) {
                    DayPosition.MonthDate -> {
                        textView.text = data.date.dayOfMonth.toString()
                        if (data.date.isBefore(today)) {
                            val info =  holidayList.find { li-> data.date.toString().equals(li.holidayDate,true)}
                            when{
                                data.date.toString()== weekEndList.find { data.date.toString().equals(it,true) }-> {
                                    val day = convertDateFormat(data.date.toString(), YYYY_MM_DD, EE)
                                    if (day.equals(resources.getString(R.string.sun),true)){
                                        roundBgView.applyBackground(holidayBackground)
                                    }else{
                                        roundBgView.applyBackground(weekEndBackground)
                                    }
                                    textView.setTextColorRes(R.color.weekend_color)
                                    textView.isClickable = false
                                }
                                data.date.toString()== info?.holidayDate -> {
                                    textView.setTextColorRes(R.color.weekend_color)
                                    roundBgView.applyBackground(holidayBackground)
                                    textView.setOnClickListener {
                                        holidayDetail(data.date, info.name ?: "")
                                    }
                                }
                                data.date.toString()== leaveList.find { data.date.toString().equals(it,true) }-> {
                                    textView.setTextColorRes(R.color.black)
                                    roundBgView.applyBackground(leaveBackground)
                                    textView.isClickable = false
                                }
                                data.date.toString()== workingDayList.find { data.date.toString().equals(it,true) }-> {
                                    textView.setTextColorRes(R.color.weekend_color)
                                    roundBgView.applyBackground(workingDay)
                                    textView.isClickable = false
                                }

                                else -> textView.setTextColorRes(R.color.weekend_color)



                            }

                           // textView.setTextColorRes(R.color.weekend_color)
                        }
                        else {
                            val info =  holidayList.find { li-> data.date.toString().equals(li.holidayDate,true)}



                            when {
                                /** priority
                                 *Job Holiday, Leave, Weekend */
                                data.date.toString()== workingDayList.find { data.date.toString().equals(it,true) }-> {
                                    textView.setTextColorRes(R.color.white)
                                    roundBgView.applyBackground(workingDay)
                                    textView.isClickable = false
                                }

                                data.date.toString()== weekEndList.find { data.date.toString().equals(it,true) }-> {
                                    val day = convertDateFormat(data.date.toString(), YYYY_MM_DD, EE)
                                    if (day.equals(resources.getString(R.string.sun),true)){
                                        roundBgView.applyBackground(holidayBackground)
                                        textView.setTextColorRes(android.R.color.white)
                                    }else{
                                        roundBgView.applyBackground(weekEndBackground)
                                        textView.setTextColorRes(android.R.color.white)
                                    }
                                }
                                data.date == today -> {
                                    textView.setTextColorRes(R.color.white)
                                    roundBgView.applyBackground(todayBackground)
                                    //textView.setTextColorRes(android.R.color.black)
                                }
                                data.date.toString()== info?.holidayDate -> {
                                    textView.setTextColorRes(android.R.color.white)
                                    roundBgView.applyBackground(holidayBackground)
                                    textView.setOnClickListener {
                                        holidayDetail(data.date, info.name ?: "")
                                    }
                                }
                                data.date.toString()== leaveList.find { data.date.toString().equals(it,true) }-> {
                                    textView.setTextColorRes(android.R.color.black)
                                    roundBgView.applyBackground(leaveBackground)
                                }

                                data.date == startDate -> {
                                    textView.setTextColorRes(R.color.black)
                                    continuousBgView.applyBackground(rangeStartBackground)
                                    roundBgView.applyBackground(singleBackground)
                                }
                                startDate != null && endDate != null && (data.date > startDate && data.date < endDate) -> {
                                    textView.setTextColorRes(R.color.grey_li)
                                    continuousBgView.applyBackground(rangeMiddleBackground)
                                }
                                data.date == endDate -> {
                                    textView.setTextColorRes(R.color.black)
                                    continuousBgView.applyBackground(rangeEndBackground)
                                    roundBgView.applyBackground(singleBackground)
                                }

                                else -> textView.setTextColorRes(R.color.white)
                            }
                        }
                    }
                    DayPosition.InDate ->
                        if (startDate != null && endDate != null &&
                            isInDateBetweenSelection(data.date, startDate, endDate)
                        ) {
                            continuousBgView.applyBackground(rangeMiddleBackground)
                        }
                    DayPosition.OutDate ->
                        if (startDate != null && endDate != null &&
                            isOutDateBetweenSelection(data.date, startDate, endDate)
                        ) {
                            continuousBgView.applyBackground(rangeMiddleBackground)
                        }
                }
            }

            private fun View.applyBackground(drawable: Drawable) {
                makeVisible()
                background = drawable
            }
        }

        binding.exFourCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewHeaderContainer> {
                override fun create(view: View) = MonthViewHeaderContainer(view)
                override fun bind(container: MonthViewHeaderContainer, data: CalendarMonth) {
                    container.textView.text = convertDateFormat(data.yearMonth.toString(), YYYY_MM,
                        MMM_YYY)
                }
            }
        bindSummaryViews()
    }
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay // Will be set when this container is bound.
        val bind = CalendarDayBinding.bind(view)

        init {
            view.setOnClickListener {
                if (day.position == DayPosition.MonthDate &&
                    (day.date == today || day.date.isAfter(today))
                ) {
                    val leave = leaveList.find { day.date.toString() == it }
                    val weekend = weekEndList.find { day.date.toString() == it }
                    val holiday = holidayList.find { li ->
                        day.date.toString().equals(li.holidayDate, true)
                    }?.holidayDate
                    if (leave.isNullOrEmpty() && weekend.isNullOrEmpty() && holiday.isNullOrEmpty()){
                        selection = getSelection(
                            clickedDate = day.date,
                            dateSelection = selection,
                        )
                        binding.exFourCalendar.notifyCalendarChanged()
                        bindSummaryViews()
                    }
                }
            }
        }
    }
    private fun bindSummaryViews() {
        binding.btnApplyLeave.isEnabled = selection.startDate != null
    }

    private fun holidayDetail(date: LocalDate,name:String){
        HolidayDetail(date,name).show(childFragmentManager,"holidayDetail")
    }

}