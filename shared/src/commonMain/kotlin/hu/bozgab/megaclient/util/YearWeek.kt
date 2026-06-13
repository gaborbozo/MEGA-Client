package hu.bozgab.megaclient.util

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class YearWeek(val year: Int, val week: Int) {

    fun next(): YearWeek {
        val maxWeeks = weeksInYear(year)

        return if (week < maxWeeks) {
            YearWeek(year, week + 1)
        } else {
            YearWeek(year + 1, 1)
        }
    }

    fun previous(): YearWeek {
        return if (week > 1) {
            YearWeek(year, week - 1)
        } else {
            YearWeek(year - 1, weeksInYear(year - 1))
        }
    }

    fun isCurrentYearWeek() = year == currentYear() && week == currentWeek()

    companion object {
        fun current(): YearWeek = YearWeek(currentYear(), currentWeek())
    }
}

fun weeksInYear(year: Int): Int {
    val jan1 = LocalDate(year, 1, 1)

    return when (jan1.dayOfWeek) {
        DayOfWeek.THURSDAY -> 53
        DayOfWeek.WEDNESDAY if isLeapYear(year) -> 53
        else -> 52
    }
}

fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

private fun currentYear(): Int =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year

private fun currentWeek(): Int =
    getIsoWeek(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

private fun getIsoWeek(date: LocalDate): Int {
    val dayOfYear = date.dayOfYear
    val dayOfWeek = date.dayOfWeek.ordinal + 1
    val week = (dayOfYear - dayOfWeek + 10) / 7
    return when {
        week < 1 -> 52
        week > 52 -> 1
        else -> week
    }
}