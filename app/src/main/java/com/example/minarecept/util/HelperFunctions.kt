package com.example.minarecept.util

import android.util.Log
import android.util.Patterns
import androidx.core.text.HtmlCompat
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.time.Duration

fun getDomainName(url: String): String {
    if (!isValidUrl(url)) return "Recept"

    val uri = URI(url)
    val domain: String = uri.host
    return if (domain.startsWith("www.")) domain.substring(4) else domain
}

fun isValidUrl(url: String?): Boolean {
    url ?: return false
    return Patterns.WEB_URL.matcher(url).matches()
}

fun isUrlImage(stringUrl: String): Boolean {
    val isSvgOrEmpty = stringUrl.endsWith("svg") || stringUrl.isEmpty()
    if (isSvgOrEmpty) return false

    var urlConnection: HttpURLConnection? = null
    System.setProperty("http.keepAlive", "false")
    return try {
        val url = URL(stringUrl)
        urlConnection = url.openConnection() as HttpURLConnection
        val contentType = urlConnection.getHeaderField("Content-Type")
        contentType.startsWith("image/")
    } catch (e: MalformedURLException) {
        e.printStackTrace()
        false
    } catch (e: IOException) {
        e.printStackTrace()
        false
    } finally {
        urlConnection?.disconnect()
    }
}

fun extractNumbers(string: String): String {
    return string.filter { it.isDigit() || it.toString() == "-" }
}

fun cleanString(str: String): String {
    return HtmlCompat.fromHtml(
        str.removeSurrounding('"'.toString(), '"'.toString()), HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString()
}


fun getHoursFromDuration(duration: Duration?): Int {
    duration ?: return 0
    return try {
        (duration.toHours() % 24L).toInt()
    } catch (e: Exception) {
        Log.e("getHoursFromDuration", e.message.toString())
        0
    }
}

fun getMinutesFromDuration(duration: Duration?): Int {
    duration ?: return 0
    return try {
        (duration.toMinutes() % 60L).toInt()
    } catch (e: Exception) {
        Log.e("getMinutesFromDuration", e.message.toString())
        0
    }
}

fun humanReadableDuration(duration: Duration?): String {
    val minutes = getMinutesFromDuration(duration)
    val hours = getHoursFromDuration(duration)
    return if (hours == 0 && minutes != 0) {
        "$minutes min"
    } else if (minutes == 0 && hours != 0) {
        "$hours h"
    } else if (hours != 0 && minutes != 0) {
        "$hours h $minutes min"
    } else {
        ""
    }
}

fun getDurationFromHourAndMinutes(hour: Int, minute: Int): Duration? {
    return try {
        val totalMinutes = (hour*60 + minute).toLong()
        Duration.ofMinutes(totalMinutes)
    } catch (e: Exception) {
        Log.e("getDurationFromHourAndMinutes", e.message.toString())
        null
    }
}
