package com.strand.minarecept.data.remote

import org.jsoup.Jsoup
import org.jsoup.nodes.Node

enum class Scores(val value: Int) {
    LOW(3),
    MEDIUM(7),
    HIGH(10)
}

class ScoreIngredient {
     fun isIngredient(node: Node): Pair<Boolean, Double> {
         val html = node.outerHtml()
         val doc = Jsoup.parse(html)
         val ingredient = doc.body().text().trim()

         // The following are the scores of each check that will get added up to a totalScore
         val nodeNameScore = if (nodeNameCheck(node)) Scores.MEDIUM.value else 0
         val attributeScore = if (attributeCheck(node)) Scores.LOW.value else 0
         val lengthScore = if (tooLong(ingredient)) 0 else Scores.HIGH.value
         val sentenceScore = if (tooManySentences(ingredient)) 0 else Scores.HIGH.value
         val numberScore = if (startWithNumber(ingredient)) Scores.MEDIUM.value else 0
         val foodWordScore = if (wordUsage(ingredient)) Scores.MEDIUM.value else 0
         val unitScore = if (containUnit(ingredient)) Scores.HIGH.value else 0

         val possibleScore = Scores.LOW.value*1 + Scores.MEDIUM.value*3 + Scores.HIGH.value*3

         val scoreList = listOf(
             nodeNameScore,
             attributeScore,
             lengthScore,
             sentenceScore,
             numberScore,
             foodWordScore,
             unitScore
         )
         val totalScore = scoreList.sum()

         val normalizedScore = totalScore.toDouble()/possibleScore.toDouble()

         return Pair(normalizedScore > 0.60, normalizedScore)
     }

    private fun nodeNameCheck(node: Node): Boolean {
        val tag = node.nodeName()
        return tag in listOf("span", "li", "ul", "table", "tbody", "tr", "td") // true if it contains one of these
    }

    private fun attributeCheck(node: Node): Boolean {
        return node.attr("class").contains("ingredient", ignoreCase = true) // true if it contains attrValue
    }

    private fun tooLong(ingredient: String): Boolean {
        return ingredient.split(" ").size >= 8 // if more than 8 words -> true
    }

    private fun tooManySentences(ingredient: String): Boolean {
        return ingredient.split(". ").isNotEmpty() // if more than 1 sentence -> true
    }
    private fun startWithNumber(ingredient: String): Boolean {
        return if (ingredient.isNotEmpty()) {
            ingredient.trim()[0].isDigit() || ingredient.trim().startsWith("-")
        } else {
            false
        }
    }

    private fun wordUsage(ingredient: String): Boolean {
        val foodWords = listOf("salt", "peppar", "vatten", "olja", "ris", "potatis", "pasta", "lax", "torsk", "kyckling")
        val words = ingredient.split(" ")
        var match = false
        val regex = Regex("\\b(?:${foodWords.joinToString(separator = "|")})\\b")
        words.forEach {
            if (regex.containsMatchIn(it)) { match = true }
        }
        return match // true if it contains some food word
    }

    private fun containUnit(ingredient: String): Boolean {
        val units = listOf("gram", "g", "kg", "l", "dl", "cl", "ml", "tsk", "msk", "krm", "st", "port", "kruka")
        val words = ingredient.split(" ")

        var match = false
        val regex = Regex("\\b(?:${units.joinToString(separator = "|")})\\b")
        words.forEach {
            if (regex.matches(it)) { match = true }
        }
        return match // true if it contains some unit
    }
}


class ScoreInstruction {

    fun isInstruction(node: Node): Pair<Boolean, Double> {
        val html = node.outerHtml()
        val doc = Jsoup.parse(html)
        val instruction = doc.body().text().trim()

        // The following are the scores of each check that will get added up to a totalScore
        val nodeNameScore = if (nodeNameCheck(node)) Scores.LOW.value else 0
        val attributeScore = if (attributeCheck(node)) Scores.MEDIUM.value else 0
        val tooShortScore = if (tooShort(instruction)) 0 else Scores.HIGH.value
        val tooLongScore = if (tooLong(instruction)) 0 else Scores.MEDIUM.value
        val sentenceScore = if (multipleSentences(instruction)) Scores.MEDIUM.value else 0
        val upperCaseScore = if (startsWithUpperCase(instruction)) Scores.LOW.value else 0
        val instructionWordScore = if (wordUsage(instruction)) Scores.HIGH.value else 0
        val punctuationScore = if (endInPunctuation(instruction)) Scores.LOW.value else 0

        val possibleScore = Scores.LOW.value*3 + Scores.MEDIUM.value*3 + Scores.HIGH.value*2

        val scoreList = listOf(
            nodeNameScore,
            attributeScore,
            tooShortScore,
            tooLongScore,
            sentenceScore,
            upperCaseScore,
            instructionWordScore,
            punctuationScore
        )
        val totalScore = scoreList.sum()

        val normalizedScore = totalScore.toDouble()/possibleScore.toDouble()

        return Pair(normalizedScore > 0.60, normalizedScore)
    }

    private fun nodeNameCheck(node: Node): Boolean {
        /**
         * Returns true the nodes nodeName is one of given.
         */
        val tag = node.nodeName()
        return tag in listOf("li", "ul", "ol", "table", "tbody", "tr", "td")
    }

    private fun attributeCheck(node: Node): Boolean {
        /**
         * Returns true if the class contains one of the keyWords
         */
        val attr = node.attr("class")
        val keyWords = listOf("instruction", "step")
        return keyWords.any { attr.contains(it, ignoreCase = true) }
    }

    private fun tooShort(instruction: String): Boolean {
        /**
         * Returns true if the instruction is shorter than 100 chars.
         */
        return instruction.length <= 100
    }

    private fun tooLong(instruction: String): Boolean {
        return instruction.length >= 500 // if more than 500 chars -> true
    }

    private fun multipleSentences(instruction: String): Boolean {
        return instruction.split(". ").size >= 2 // if more than/equal to 2 sentences -> true
    }

    private fun endInPunctuation(instruction: String): Boolean {
        return instruction.trim().endsWith(".") // true if it ends with "."
    }

    private fun startsWithUpperCase(instruction: String): Boolean {
        return if (instruction.isNotEmpty()) {
            instruction.trim()[0].isUpperCase() || instruction.trim()[0].isDigit()
        } else {
            false
        }
    }

    private fun wordUsage(instruction: String): Boolean {
        val instructionWords = listOf("skär", "hacka", "marinera", "koka", "stek", "blanda", "vispa", "tillsätt", "mixa", "sila", "skala", "sjud", "grader")
        val words = instruction.split(" ")
        var match = false
        val regex = Regex("\\b(?:${instructionWords.joinToString(separator = "|")})\\b", RegexOption.IGNORE_CASE)
        words.forEach {
            if (regex.containsMatchIn(it)) { match = true }
        }
        return match // true if it contains some instruction word
    }



}

