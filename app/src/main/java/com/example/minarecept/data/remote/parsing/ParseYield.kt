package com.example.minarecept.data.remote.parsing

import android.util.Log
import com.example.minarecept.util.cleanString
import com.example.minarecept.util.extractNumbers
import com.google.gson.JsonObject

class ParseYield {

    fun getYield(jsonLd: JsonObject): Int? {
        return try {
            extractNumbers( jsonYield(jsonLd) ).toIntOrNull()
        } catch (e: Exception) {
            Log.d("Yield", e.message.toString())
            null
        }
    }

    private fun jsonYield(obj: JsonObject): String {
        return cleanString(obj.get("recipeYield").toString())
    }
}