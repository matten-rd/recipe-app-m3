package com.strand.minarecept.data.remote.parsing

import android.util.Log
import com.google.gson.JsonObject
import com.strand.minarecept.util.cleanString
import com.strand.minarecept.util.extractNumbers

class ParseYield {

    fun getYield(jsonLd: JsonObject): Int? {
        return try {
            jsonYield(jsonLd).extractNumbers().toIntOrNull()
        } catch (e: Exception) {
            Log.d("Yield", e.message.toString())
            0
        }
    }

    private fun jsonYield(obj: JsonObject): String {
        return cleanString(obj.get("recipeYield").toString())
    }
}