//
//   Calendar Notifications Plus
//   Copyright (C) 2017 Sergey Parshin (s.parshin.sc@gmail.com)
//
//   This program is free software; you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published by
//   the Free Software Foundation; either version 3 of the License, or
//   (at your option) any later version.
//
//   This program is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//   GNU General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with this program; if not, write to the Free Software Foundation,
//   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
//

package com.github.quarck.calnotify.addevent.storage

import com.github.quarck.calnotify.Consts

data class NewEventReminder(val time: Long, val isEmail: Boolean) {

    fun serialize() = "$time,${if(isEmail) 1 else 0}"

    companion object {
        fun deserialize(str: String): NewEventReminder {
            val (time, isEmail) = str.split(',')
            return NewEventReminder(
                    time = time.toLong(),
                    isEmail = isEmail.toInt() != 0
            )
        }
    }

    val allDayDaysBefore: Int
        get() = ((this.time + Consts.DAY_IN_MILLISECONDS) / Consts.DAY_IN_MILLISECONDS).toInt()

    val allDayHourOfDayAndMinute: Pair<Int, Int>
        get() {
            val timeOfDayMillis =
                    if (this.time >= 0L) { // on the day of event
                        Consts.DAY_IN_MILLISECONDS - this.time % Consts.DAY_IN_MILLISECONDS
                    }
                    else  {
                        -this.time
                    }

            val timeOfDayMinutes = timeOfDayMillis.toInt() / 1000 / 60

            val minute = timeOfDayMinutes % 60
            val hourOfDay = timeOfDayMinutes / 60

            return Pair(hourOfDay, minute)
        }
}

fun List<NewEventReminder>.serialize()
        = this.map { it.serialize() }.joinToString(separator = ";")

fun String.deserializeNewEventReminders()
        = this.split(";").map { NewEventReminder.deserialize(it) }.toList()


data class NewEventRecord(
        var id: Long,
        var eventId: Long,
        val calendarId: Long,
        val title: String,
        val desc: String,
        val location: String,
        val timezone: String,
        val startTime: Long,
        val endTime: Long,
        val isAllDay: Boolean,
        val repeatingRule: String, // empty if not repeating
        val colour: Int,
        val reminders: List<NewEventReminder>
)