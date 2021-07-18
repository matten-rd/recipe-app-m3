package com.example.minarecept.data.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ListTypeConverter {

    @TypeConverter
    fun toList(listOfString: String?): List<String>? {
        listOfString ?: return null
        return Gson().fromJson(listOfString, object : TypeToken<List<String?>?>() {}.type)
    }

    @TypeConverter
    fun fromList(listOfString: List<String>?): String? {
        listOfString ?: return null
        return Gson().toJson(listOfString)
    }

}