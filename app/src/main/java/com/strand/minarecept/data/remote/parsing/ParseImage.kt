package com.strand.minarecept.data.remote.parsing

import android.graphics.BitmapFactory
import com.strand.minarecept.util.isUrlImage
import org.jsoup.nodes.Document
import java.net.URL

class ParseImage {

    fun getImages(document: Document): List<String> {
        // First try to get the correct image from the metadata.
        val ogImage = document.select("meta[property='og:image'], meta[name='og:image']")
            .attr("content").toString()
        if (ogImage.isNotEmpty()) return listOf(ogImage)
        // Then just get the largest image. // TODO: Implement image selector.
        // TODO: Make this part more efficient.
        val media = document.select("img[src~=(?i)\\.(png|jpe?g)]") // get .png .jpg .jpeg
            .distinctBy { it.attr("abs:src").toString() } // remove duplicates
        val mediaLinks = mutableListOf<String>()
        for (src in media) {
            val isValidImage = isUrlImage(src.attr("abs:src").toString())
            if (isValidImage) {
                println(" * ${src.tagName()}: <${src.attr("abs:src")}>")
                mediaLinks.add(src.attr("abs:src").toString())
            }
        }
        if (mediaLinks.isNotEmpty()) {
            // TODO: Move the url and bmp out - to not call twice
            val largeMedia = mediaLinks.filter {
                val url = URL(it)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                println("${bmp.width} X ${bmp.height} <${url.path.replaceAfterLast("/", "")}>")
                bmp.width > 200 && bmp.height > 200
            }.sortedBy {
                val url = URL(it)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                bmp.width
            }
            if (largeMedia.isNotEmpty())
                return largeMedia // #1 return large images
            else
                return mediaLinks.toList() // #2 return smaller images
        } else {
            return listOf("https://picsum.photos/600/600") // #3 return random image
        }
    }

}