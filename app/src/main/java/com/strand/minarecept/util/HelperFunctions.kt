package com.strand.minarecept.util

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.core.text.HtmlCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import com.strand.minarecept.data.remote.parsing.ParseTime
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt

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

fun String.extractNumbers(): String {
    /**
     * Regex som följer:
     *  1. kolla komma separerat
     *  2. kolla punkt separerat
     *  3. kolla slash separerat
     *  4. kolla positiva och negativa tal
     */
    val p: Pattern = Pattern.compile("\\d+,?\\d+|\\d+.?\\d+|\\d+/?\\d+|-?\\d+")
    val m: Matcher = p.matcher(this)
    return if (m.find()) {
        try {
            m.group()
        } catch (e: IllegalStateException) { "" }
    } else {
        ""
    }
}

fun String.roundToIntOrFloat(): String {
    // FIXME: This can fail in some circumstances
    val newString = this.replace(",", ".")
    if (newString.toFloatOrNull() != null) {
        if (newString.toFloat() % newString.toFloat().roundToInt() == 0f)
            return newString.toFloat().roundToInt().toString()
        else
            return String.format("%.1f", newString.toFloat())
    } else if (newString.toIntOrNull() != null) {
        return newString
    } else {
        return ""
    }
}

fun cleanString(str: String): String {
    return HtmlCompat.fromHtml(
        str.removeSurrounding('"'.toString(), '"'.toString()), HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString()
}


fun getHoursFromDuration(durationString: String?): Int {
    durationString ?: return 0
    return try {
        val duration = ParseTime().getDuration(durationString)
        duration ?: return 0
        (duration.toHours() % 24L).toInt()
    } catch (e: Exception) {
        Log.e("getHoursFromDuration", e.message.toString())
        0
    }
}

fun getMinutesFromDuration(durationString: String?): Int {
    durationString ?: return 0
    return try {
        val duration = ParseTime().getDuration(durationString)
        duration ?: return 0
        (duration.toMinutes() % 60L).toInt()
    } catch (e: Exception) {
        Log.e("getMinutesFromDuration", e.message.toString())
        0
    }
}

fun humanReadableDuration(durationString: String?): String {
    val minutes = getMinutesFromDuration(durationString)
    val hours = getHoursFromDuration(durationString)
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

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word ->
        word.lowercase(Locale.getDefault())
        .replaceFirstChar { char ->
            if (char.isLowerCase())
                char.titlecase(Locale.getDefault())
            else
                char.toString()
        }
    }


fun Uri.uploadToFirebaseAndGetRef(): Task<Uri> {
    // TODO: crop image before uploading
    val storage = Firebase.storage
    val imagesRef: StorageReference = storage.reference.child("images")
    val fileRef = imagesRef.child("${this.lastPathSegment}")
    val uploadTask = fileRef.putFile(this)
        .addOnProgressListener { (bytesTransferred, totalByteCount) ->
            val progress = (100.0 * bytesTransferred) / totalByteCount
            Log.d("Firebase", "Upload is $progress% done")
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Upload failed", e)
        }

    return uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }
        fileRef.downloadUrl
    }
}

fun String.isValidEmail() =
    this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword() =
    this.length >= 8 && this.isNotBlank()
            && this.any { it.isDigit() } && this.any { it.isUpperCase() }
            && this.any { !it.isDigit() } && this.any { it.isLowerCase() }



data class Ingredient(
    val unit: String = "",
    val amount: Float? = null,
    val name: String = ""
)

fun String.formatIngredient(): Ingredient {
    var formattedIngredient = Ingredient(
        amount = parseDivString(this.extractNumbers())
            .replace(",", ".")
            .toFloatOrNull()
    )
    val ingredientList = this.split(" ")
    val possibleUnits = listOf(
        "kg", "kilo", "kilogram", "g", "gram", "mg",
        "l", "liter", "dl", "deciliter", "cl", "centiliter", "ml", "milliliter",
        "msk", "matsked", "tsk", "tesked", "krm", "kryddmått",
        "st", "stycken", "dussin",
        "tkp", "tekopp", "kkp", "kaffekopp"
    )
    ingredientList.forEach { entry ->
        formattedIngredient = if (entry in possibleUnits) {
            Ingredient(
                unit = entry,
                amount = formattedIngredient.amount,
                name = formattedIngredient.name
            )
        } else if (entry != this.extractNumbers()) {
            Ingredient(
                unit = formattedIngredient.unit,
                amount = formattedIngredient.amount,
                name = formattedIngredient.name + " " + entry
            )
        } else {
            Ingredient(
                unit = formattedIngredient.unit,
                amount = formattedIngredient.amount,
                name = formattedIngredient.name
            )
        }
    }
    return formattedIngredient
}

fun parseDivString(ratio: String): String {
    return if (ratio.contains("/")) {
        val rat = ratio.split("/").toTypedArray()
        val double = rat[0].toDouble() / rat[1].toDouble()
        double.toString()
    } else {
        ratio
    }
}