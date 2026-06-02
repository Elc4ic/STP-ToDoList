package dev.stp.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import dev.stp.app.presentation.components.MyCalendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class MyCalendarWhiteBoxTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val zoneId: ZoneId = ZoneId.systemDefault()

    private fun epoch(year: Int, month: Int, day: Int): Long =
        LocalDate.of(year, month, day)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

    private fun renderCalendar(
        currMonth: YearMonth,
        selectedStartDate: Long? = null,
        selectedEndDate: Long? = null,
        onDayClick: (Long) -> Unit = {}
    ) {
        composeRule.setContent {
            MaterialTheme {
                MyCalendar(
                    currMonth = currMonth,
                    selectedStartDate = selectedStartDate,
                    selectedEndDate = selectedEndDate,
                    onDayClick = onDayClick
                )
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun path1_emptySelection_rendersFullSixWeekCalendarAndHandlesCurrentMonthWeekdayClick() {
        var clickedDate: Long? = null

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = null,
            selectedEndDate = null,
            onDayClick = { clickedDate = it }
        )

        composeRule.onAllNodes(hasClickAction()).assertCountEquals(42)
        composeRule.onAllNodesWithText("15").assertCountEquals(1)
        composeRule.onAllNodesWithText("15").onFirst().performClick()

        assertEquals(epoch(2025, 6, 15), clickedDate)
    }

    @Test
    fun path2_selectedDateMatchesStartDate_coversFirstOperandOfIsSelected() {
        var clickedDate: Long? = null
        val start = epoch(2025, 6, 15)

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = start,
            selectedEndDate = null,
            onDayClick = { clickedDate = it }
        )

        composeRule.onAllNodesWithText("15").onFirst().performClick()

        assertEquals(start, clickedDate)
    }

    @Test
    fun path3_selectedDateMatchesEndDate_coversSecondOperandOfIsSelected() {
        var clickedDate: Long? = null
        val start = epoch(2025, 6, 10)
        val end = epoch(2025, 6, 15)

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = start,
            selectedEndDate = end,
            onDayClick = { clickedDate = it }
        )

        composeRule.onAllNodesWithText("15").onFirst().performClick()

        assertEquals(end, clickedDate)
    }

    @Test
    fun path4_startDateExistsButEndDateIsNull_coversIsInRangeSecondOperandFalse() {
        var clickedDate: Long? = null
        val start = epoch(2025, 6, 10)
        val dayInsidePotentialRange = epoch(2025, 6, 15)

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = start,
            selectedEndDate = null,
            onDayClick = { clickedDate = it }
        )

        composeRule.onAllNodesWithText("15").onFirst().performClick()

        assertEquals(dayInsidePotentialRange, clickedDate)
    }

    @Test
    fun path5_rangeExistsButDateIsNotAfterStart_coversIsAfterFalse() {
        var clickedDate: Long? = null
        val start = epoch(2025, 6, 10)
        val end = epoch(2025, 6, 20)

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = start,
            selectedEndDate = end,
            onDayClick = { clickedDate = it }
        )

        composeRule.onAllNodesWithText("10").onFirst().performClick()

        assertEquals(start, clickedDate)
    }

    @Test
    fun path6_rangeExistsDateAfterStartButNotBeforeEnd_coversIsBeforeFalse() {
        var clickedDate: Long? = null
        val start = epoch(2025, 6, 10)
        val end = epoch(2025, 6, 20)
        val afterEnd = epoch(2025, 6, 25)

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = start,
            selectedEndDate = end,
            onDayClick = { clickedDate = it }
        )

        composeRule.onAllNodesWithText("25").onFirst().performClick()

        assertEquals(afterEnd, clickedDate)
    }

    @Test
    fun path7_rangeExistsAndDateIsInsideRange_coversIsInRangeTrue() {
        var clickedDate: Long? = null
        val start = epoch(2025, 6, 10)
        val end = epoch(2025, 6, 20)
        val insideRange = epoch(2025, 6, 15)

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = start,
            selectedEndDate = end,
            onDayClick = { clickedDate = it }
        )

        composeRule.onAllNodesWithText("15").onFirst().performClick()

        assertEquals(insideRange, clickedDate)
    }

    @Test
    fun path8_otherMonthDate_coversDayTypeOtherMonthBranch() {
        var clickedDate: Long? = null
        val previousMonthDate = epoch(2025, 4, 27)

        renderCalendar(
            currMonth = YearMonth.of(2025, 5),
            selectedStartDate = null,
            selectedEndDate = null,
            onDayClick = { clickedDate = it }
        )

        // May 2025 calendar grid starts with Sunday, 27 April 2025.
        composeRule.onAllNodesWithText("27").onFirst().performClick()

        assertEquals(previousMonthDate, clickedDate)
    }

    @Test
    fun path9_currentMonthSunday_coversDayTypeSundayBranch() {
        var clickedDate: Long? = null
        val sunday = epoch(2025, 6, 1)

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = null,
            selectedEndDate = null,
            onDayClick = { clickedDate = it }
        )

        // June 2025 starts on Sunday; first "1" in the grid is 1 June 2025.
        composeRule.onAllNodesWithText("1").onFirst().performClick()

        assertEquals(sunday, clickedDate)
    }

    @Test
    fun path10_currentMonthWeekday_coversDayTypeWeekdayElseBranch() {
        var clickedDate: Long? = null
        val weekday = epoch(2025, 6, 16)

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = null,
            selectedEndDate = null,
            onDayClick = { clickedDate = it }
        )

        composeRule.onAllNodesWithText("16").onFirst().performClick()

        assertEquals(weekday, clickedDate)
    }

    @Test
    fun path11_calendarCallbackIsCalledForDifferentRows_coversNestedLoopContinuation() {
        val clickedDates = mutableListOf<Long>()

        renderCalendar(
            currMonth = YearMonth.of(2025, 6),
            selectedStartDate = null,
            selectedEndDate = null,
            onDayClick = { clickedDates.add(it) }
        )

        composeRule.onAllNodesWithText("1").onFirst().performClick()
        composeRule.onAllNodesWithText("15").onFirst().performClick()
        composeRule.onAllNodesWithText("30").onFirst().performClick()

        assertEquals(3, clickedDates.size)
        assertTrue(clickedDates.contains(epoch(2025, 6, 1)))
        assertTrue(clickedDates.contains(epoch(2025, 6, 15)))
        assertTrue(clickedDates.contains(epoch(2025, 6, 30)))
        assertNotNull(clickedDates.lastOrNull())
    }
}
