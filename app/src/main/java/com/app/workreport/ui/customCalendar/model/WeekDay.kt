package com.app.workreport.ui.customCalendar.model

import java.io.Serializable
import java.time.LocalDate

data class WeekDay(val date: LocalDate, val position: WeekDayPosition) : Serializable
