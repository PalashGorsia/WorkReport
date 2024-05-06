package com.app.workreport.ui.customCalendar.monthcalendar

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.ui.customCalendar.MonthHeaderFooterBinder
import com.app.workreport.ui.customCalendar.ViewContainer
import com.app.workreport.ui.customCalendar.model.CalendarDay
import com.app.workreport.ui.customCalendar.model.CalendarMonth
import com.app.workreport.ui.customCalendar.view.WeekHolder

internal class MonthViewHolder constructor(
    rootLayout: ViewGroup,
    private val headerView: View?,
    private val footerView: View?,
    private val weekHolders: List<WeekHolder<CalendarDay>>,
    private var monthHeaderBinder: MonthHeaderFooterBinder<ViewContainer>?,
    private var monthFooterBinder: MonthHeaderFooterBinder<ViewContainer>?,
) : RecyclerView.ViewHolder(rootLayout) {

    private var headerContainer: ViewContainer? = null
    private var footerContainer: ViewContainer? = null

    lateinit var month: CalendarMonth

    fun bindMonth(month: CalendarMonth) {
        this.month = month
        headerView?.let { view ->
            val headerContainer = headerContainer ?: monthHeaderBinder!!.create(view).also {
                headerContainer = it
            }
            monthHeaderBinder?.bind(headerContainer, month)
        }
        weekHolders.forEachIndexed { index, week ->
            week.bindWeekView(month.weekDays.getOrNull(index).orEmpty())
        }
        footerView?.let { view ->
            val footerContainer = footerContainer ?: monthFooterBinder!!.create(view).also {
                footerContainer = it
            }
            monthFooterBinder?.bind(footerContainer, month)
        }
    }

    fun reloadDay(day: CalendarDay) {
        weekHolders.firstOrNull { it.reloadDay(day) }
    }
}
