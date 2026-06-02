package dev.stp.app.data.mapper

import androidx.room.TypeConverter
import enums.ProgressStatus

class ProgressStatusConverter {
    @TypeConverter
    fun fromProgressStatus(status: ProgressStatus): String {
        return status.name
    }

    @TypeConverter
    fun toProgressStatus(status: String): ProgressStatus {
        return ProgressStatus.valueOf(status)
    }
}