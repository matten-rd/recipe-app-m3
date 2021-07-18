package com.example.minarecept.data.remote.parsing

import android.util.Log
import com.example.minarecept.util.cleanString
import com.google.gson.JsonObject
import org.jsoup.nodes.Document

class ParseDescription {

    fun getDescription(
        jsonLd: JsonObject,
        document: Document
    ): String {
        return try {
            jsonDescription(jsonLd)
        } catch (e: Exception) {
            Log.d("Desctiption: jsonld", e.message.toString())

            try {
                traverseDescription(document)
            } catch (e: Exception) {
                Log.d("Description: traverse", e.message.toString())
                ""
            }
        }
    }


    private fun jsonDescription(obj: JsonObject): String {
        return cleanString(obj.get("description").toString())
    }

    private fun traverseDescription(document: Document): String {
        return cleanString(
            document
                .select("meta[property='og:description'], meta[name='og:description']")
                .attr("content")
                .toString()
        )
    }

}