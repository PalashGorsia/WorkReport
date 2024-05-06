package com.app.workreport.ui.customCalendar

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.LazyThreadSafetyMode.NONE


@RequiresApi(Build.VERSION_CODES.O)
data class DateSelection(var startDate: LocalDate? = null, var endDate: LocalDate? = null) {
    val daysBetween by lazy(NONE) {
        if (startDate == null || endDate == null) null else {
            ChronoUnit.DAYS.between(startDate, endDate)
        }
    }
}