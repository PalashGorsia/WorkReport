package com.app.workreport.ui.customCalendar.view

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.core.view.isGone
import com.app.workreport.ui.customCalendar.holder.DayHolder
import com.app.workreport.ui.customCalendar.model.DaySize

internal class WeekHolder<Day>(
    private val daySize: DaySize,
    private val dayHolders: List<DayHolder<Day>>,
) {

    private lateinit var weekContainer: LinearLayout

    fun inflateWeekView(parent: LinearLayout): View {
        return WidthDivisorLinearLayout(parent.context).apply {
            weekContainer = this
            val width = if (daySize.parentDecidesWidth) MATCH_PARENT else WRAP_CONTENT
            val height = if (daySize.parentDecidesHeight) MATCH_PARENT else WRAP_CONTENT
            val weight = if (daySize.parentDecidesHeight) 1f else 0f
            layoutParams = LinearLayout.LayoutParams(width, height, weight)
            orientation = LinearLayout.HORIZONTAL
            weightSum = dayHolders.count().toFloat()
            widthDivisor = if (daySize == DaySize.Square) dayHolders.count() else 0
            for (holder in dayHolders) {
                addView(holder.inflateDayView(this))
            }
        }
    }

    fun bindWeekView(daysOfWeek: List<Day>) {
        // The last week row can be empty if out date style is not `EndOfGrid`
        weekContainer.isGone = daysOfWeek.isEmpty()
        daysOfWeek.forEachIndexed { index, day ->
            dayHolders[index].bindDayView(day)
        }
    }

    fun reloadDay(day: Day): Boolean = dayHolders.any { it.reloadViewIfNecessary(day) }
}