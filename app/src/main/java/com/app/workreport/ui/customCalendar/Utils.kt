package com.app.workreport.ui.customCalendar

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.app.workreport.databinding.CalendarFooterBinding
import com.app.workreport.databinding.CalendarHeaderBinding
import com.app.workreport.ui.customCalendar.holder.DayConfig
import com.app.workreport.ui.customCalendar.holder.DayHolder
import com.app.workreport.ui.customCalendar.model.DaySize
import com.app.workreport.ui.customCalendar.model.MarginValues
import com.app.workreport.ui.customCalendar.view.WeekHolder
import com.app.workreport.util.inflate
import java.time.LocalDate

internal data class ItemContent<Day>(
    val itemView: ViewGroup,
    val headerView: View?,
    val footerView: View?,
    val weekHolders: List<WeekHolder<Day>>,
)

internal fun <Day, Container : ViewContainer> setupItemRoot(
    itemMargins: MarginValues,
    daySize: DaySize,
    context: Context,
    dayViewResource: Int,
    itemHeaderResource: Int,
    itemFooterResource: Int,
    weekSize: Int,
    itemViewClass: String?,
    dayBinder: Binder<Day, Container>,
): ItemContent<Day> {
    val rootLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
    }

    val itemHeaderView = if (itemHeaderResource != 0) {
        rootLayout.inflate(itemHeaderResource).also { headerView ->
            rootLayout.addView(headerView)
        }
    } else null

    @Suppress("UNCHECKED_CAST")
    val dayConfig = DayConfig(
        daySize = daySize,
        dayViewRes = dayViewResource,
        dayBinder = dayBinder as Binder<Day, ViewContainer>,
    )

    val weekHolders = List(weekSize) {
        WeekHolder(dayConfig.daySize, List(7) { DayHolder(dayConfig) })
    }.onEach { weekHolder ->
        rootLayout.addView(weekHolder.inflateWeekView(rootLayout))
    }

    val itemFooterView = if (itemFooterResource != 0) {
        rootLayout.inflate(itemFooterResource).also { footerView ->
            rootLayout.addView(footerView)
        }
    } else null

    fun setupRoot(root: ViewGroup) {
        val width = if (daySize.parentDecidesWidth) MATCH_PARENT else WRAP_CONTENT
        val height = if (daySize.parentDecidesHeight) MATCH_PARENT else WRAP_CONTENT
        root.layoutParams = MarginLayoutParams(width, height).apply {
            bottomMargin = itemMargins.bottom
            topMargin = itemMargins.top
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                marginStart = itemMargins.start
                marginEnd = itemMargins.end
            } else {
                leftMargin = itemMargins.start
                rightMargin = itemMargins.end
            }
        }
    }

    val itemView = itemViewClass?.let {
        val customLayout = runCatching {
            Class.forName(it)
                .getDeclaredConstructor(Context::class.java)
                .newInstance(rootLayout.context) as ViewGroup
        }.onFailure { Log.e("CalendarView", "failure loading custom class", it) }
            .getOrNull()

        customLayout?.apply {
            setupRoot(this)
            addView(rootLayout)
        }
    } ?: rootLayout.apply { setupRoot(this) }

    return ItemContent(
        itemView = itemView,
        headerView = itemHeaderView,
        footerView = itemFooterView,
        weekHolders = weekHolders,
    )
}


fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun dpToPx(dp: Int, context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        context.resources.displayMetrics,
    ).toInt()

internal val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

internal val Context.inputMethodManager
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int): Drawable =
    requireNotNull(ContextCompat.getDrawable(this, drawable))

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

/*fun Fragment.addStatusBarColorUpdate(@ColorRes colorRes: Int) {
    view?.findViewTreeLifecycleOwner()?.lifecycle?.addObserver(
        StatusBarColorLifecycleObserver(
            requireActivity(),
            requireContext().getColorCompat(colorRes),
        ),
    )
}*/


class MonthViewFooterContainer(view: View) : ViewContainer(view) {
    val footerLayout = CalendarFooterBinding.bind(view).root
}
class MonthViewHeaderContainer(view: View) : ViewContainer(view) {
    val textView = CalendarHeaderBinding.bind(view).exFourHeaderText
}

internal fun dayTag(date: LocalDate): Int = date.hashCode()
