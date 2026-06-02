package dev.stp.app.data.mapper

import androidx.room.TypeConverter
import java.util.UUID

class UuidConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuidString: String?): UUID? {
        return try {
            uuidString?.let { UUID.fromString(it) }
        } catch (e: Exception) {
            null
        }
    }
}