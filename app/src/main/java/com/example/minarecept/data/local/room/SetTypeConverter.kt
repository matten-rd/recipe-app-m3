package com.example.minarecept.data.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SetTypeConverter {

    @TypeConverter
    fun toSet(setOfString: String?): Set<String>? {
        setOfString ?: return null
        return Gson().fromJson(setOfString, object : TypeToken<Set<String?>?>() {}.type)
    }

    @TypeConverter
    fun fromSet(setOfString: Set<String>?): String? {
        setOfString ?: return null
        return Gson().toJson(setOfString)
    }

}