package com.example.minarecept.data.remote.parsing

import android.util.Log
import com.example.minarecept.util.cleanString
import com.google.gson.JsonObject
import java.time.Duration

class ParseTime {

    fun getTime(jsonLd: JsonObject): Duration? {
        return try {
            getDuration( jsonTime(jsonLd) )
        } catch (e: Exception) {
            Log.d("Time", e.message.toString())
            getDuration("PT0M")
        }
    }

    private fun jsonTime(obj: JsonObject): String {
        return cleanString(obj.get("totalTime").toString())
    }

    private fun getDuration(dateString: String): Duration? {
        return try {
            val cleanedDateString = dateString.replace(Regex("Y.*D"), "D")
            Duration.parse(cleanedDateString)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing duration", e)
            null
        }
    }

}

private const val TAG = "ParseTime"