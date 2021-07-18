package com.example.minarecept.data.remote.parsing

import android.util.Log
import com.example.minarecept.util.cleanString
import com.google.gson.JsonObject
import org.jsoup.nodes.Document

class ParseTitle {

    fun getTitle(
        jsonLd: JsonObject,
        document: Document
    ): String {
        return try {
            jsonTitle(jsonLd)
        } catch (e: Exception) {
            Log.d("Title: jsonld", e.message.toString())

            try {
                traverseTitle(document)
            } catch (e: Exception) {
                Log.d("Title: traverse", e.message.toString())
                ""
            }
        }
    }

    private fun traverseTitle(document: Document): String {
        val title = document.select("meta[property='og:title'], meta[name='og:title']").attr("content").toString()
        return cleanString(
            if (title.isNotEmpty()) title else document.title().toString()
        )
    }

    private fun jsonTitle(obj: JsonObject): String {
        return cleanString(obj.get("name").toString())
    }
}