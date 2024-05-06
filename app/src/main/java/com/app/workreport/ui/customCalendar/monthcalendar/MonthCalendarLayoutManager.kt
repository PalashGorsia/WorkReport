package com.app.workreport.ui.customCalendar.monthcalendar

import android.os.Build
import androidx.annotation.RequiresApi
import com.app.workreport.ui.customCalendar.CalendarLayoutManager
import com.app.workreport.ui.customCalendar.CalendarView
import com.app.workreport.ui.customCalendar.dayTag
import com.app.workreport.ui.customCalendar.model.CalendarDay
import com.app.workreport.ui.customCalendar.model.MarginValues
import java.time.YearMonth
@RequiresApi(Build.VERSION_CODES.O)
internal class MonthCalendarLayoutManager(private val calView: CalendarView) :
    CalendarLayoutManager<YearMonth, CalendarDay>(calView, calView.orientation) {

    private val adapter: MonthCalendarAdapter
        get() = calView.adapter as MonthCalendarAdapter

    override fun getaItemAdapterPosition(data: YearMonth): Int = adapter.getAdapterPosition(data)

    override fun getaDayAdapterPosition(data: CalendarDay): Int = adapter.getAdapterPosition(data)
    override fun getDayTag(data: CalendarDay): Int = dayTag(data.date)
    override fun getItemMargins(): MarginValues = calView.monthMargins
    override fun scrollPaged(): Boolean = calView.scrollPaged
    override fun notifyScrollListenerIfNeeded() = adapter.notifyMonthScrollListenerIfNeeded()
}
