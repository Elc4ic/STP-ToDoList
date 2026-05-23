package dev.stp.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.stp.app.presentation.ScheduleScreen.DayType

import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId


@Composable
fun MyCalendar(
    currMonth: YearMonth,
    selectedData:  Long,
    onDayClick: (Long)->Unit,
){
    val firstDayOfMonth  = remember(currMonth) {
        currMonth.atDay(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    val daysMillis = remember(currMonth) {
        (1..currMonth.lengthOfMonth()).map { day ->

            currMonth.atDay(day)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }

    }
    
    val sundays = remember(daysMillis) {
        daysMillis.filter {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .dayOfWeek
            .toString() == "SUNDAY"
                && it != firstDayOfMonth
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        if(currMonth.atDay(1).dayOfWeek.toString() =="MONDAY"){
            ScheduleDate(
                mod = DayType.OTHER_MONTH,
                date = currMonth.atDay(1)
                    .minusDays(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli(),
                selectedData = selectedData
            ) {
                onDayClick(it)
            }

            repeat(6){
                ScheduleDate(
                     date = firstDayOfMonth + it * 86_400_000L,
                    selectedData = selectedData,
                    mod = DayType.WEEKDAY
                ){
                    onDayClick(it)
                }

            }
        }else if(currMonth.atDay(1).dayOfWeek.toString() =="TUESDAY"){
            for(i in 2 downTo 1){
                ScheduleDate(
                    mod = DayType.OTHER_MONTH,
                    date = currMonth.atDay(1)
                        .minusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    selectedData = selectedData
                ) {onDayClick(it)}

            }
            repeat(5){
                ScheduleDate(
                    date = firstDayOfMonth + it * 86_400_000L,
                    selectedData = selectedData,
                    mod = DayType.WEEKDAY
                ){
                   onDayClick(it)

                }

            }

        }else if(currMonth.atDay(1).dayOfWeek.toString() =="WEDNESDAY"){
            for(i in 3 downTo 1){
                ScheduleDate(
                    mod = DayType.OTHER_MONTH,
                    date = currMonth.atDay(1)
                        .minusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    selectedData = selectedData
                ) {onDayClick(it)}

            }
            repeat(4){
                ScheduleDate(
                    date = firstDayOfMonth + it * 86_400_000L,
                    selectedData = selectedData,
                    mod = DayType.WEEKDAY
                ){
                    onDayClick(it)

                }

            }

        }
        else if(currMonth.atDay(1).dayOfWeek.toString() =="THURSDAY"){
            for(i in 4 downTo 1){
                ScheduleDate(
                    mod = DayType.OTHER_MONTH,
                    date = currMonth.atDay(1)
                        .minusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    selectedData = selectedData
                ) {onDayClick(it)}

            }
            repeat(3){
                ScheduleDate(
                    date = firstDayOfMonth + it * 86_400_000L,
                    selectedData = selectedData,
                    mod = DayType.WEEKDAY
                ){
                    onDayClick(it)

                }

            }

        }else if(currMonth.atDay(1).dayOfWeek.toString() =="FRIDAY"){
            for(i in 5 downTo 1){
                ScheduleDate(
                    mod = DayType.OTHER_MONTH,
                    date = currMonth.atDay(1)
                        .minusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    selectedData = selectedData
                ) {onDayClick(it)}

            }
            repeat(2){
                ScheduleDate(
                    date = firstDayOfMonth + it * 86_400_000L,
                    selectedData = selectedData,
                    mod = DayType.WEEKDAY
                ){
                    onDayClick(it)

                }

            }

        }else if(currMonth.atDay(1).dayOfWeek.toString() =="SATURDAY"){
            for(i in 6 downTo 1){
                ScheduleDate(
                    mod = DayType.OTHER_MONTH,
                    date = currMonth.atDay(1)
                        .minusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    selectedData = selectedData
                ) {onDayClick(it)}

            }
            ScheduleDate(
                date = firstDayOfMonth,
                selectedData = selectedData,
                mod = DayType.WEEKDAY
            ){
                onDayClick(it)

            }
        }else{
            ScheduleDate(
                date = firstDayOfMonth,
                selectedData = selectedData,
                mod = DayType.SUNDAY
            ){
                onDayClick(it)

            }
            for(day in (1..6)){
                ScheduleDate(
                    date = (firstDayOfMonth + 86_400_000L) + day* 86_400_000L,
                    selectedData = selectedData,
                    mod = DayType.WEEKDAY
                ){
                    onDayClick(it)

                }
            }
        }

    }
    for(sunday in sundays){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            ScheduleDate(
                date = sunday,
                mod = DayType.SUNDAY,
                selectedData = selectedData
            ){
               onDayClick(it)
            }

            for(day in(1..6)){
                if( sunday == sundays.last() && sunday + day* 86_400_000L >currMonth.plusMonths(1).atDay(1).minusDays(1).atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli() ){
                    ScheduleDate(
                        mod = DayType.OTHER_MONTH,
                        date = sunday + day * 86_400_000L,
                        selectedData = selectedData
                    ){onDayClick(it)}

                }else{
                    ScheduleDate(

                        date = sunday + day * 86_400_000L,
                        selectedData = selectedData,
                        mod = DayType.WEEKDAY
                    ){onDayClick(it)}
                }


            }






        }

    }
}
