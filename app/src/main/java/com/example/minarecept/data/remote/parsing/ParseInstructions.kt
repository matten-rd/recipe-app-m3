package com.example.minarecept.data.remote.parsing

import android.util.Log
import com.example.minarecept.data.remote.ScoreInstruction
import com.example.minarecept.util.cleanString
import com.google.gson.JsonObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.select.NodeVisitor

class ParseInstructions {

    fun getInstructions(
        jsonLd: JsonObject,
        document: Document,
        ingredients: List<String>?
    ): List<String> {
        return try {
            jsonInstructions(jsonLd)
        } catch (e: Exception) {
            Log.d("Instructions: jsonld", e.message.toString())

            try {
                val ingredientList = ingredients ?: listOf<String>()
                traverseInstructions(document, ingredientList)
            } catch (e: Exception) {
                Log.d("Instructions: traverse", e.message.toString())
                listOf<String>()
            }
        }
    }

    private fun jsonInstructions(obj: JsonObject): List<String> {
        val ins: MutableList<String> = mutableListOf()

        val instructionsNode = obj.get("recipeInstructions")
        if (instructionsNode.isJsonArray) {
            val instructions = instructionsNode.asJsonArray

            if (instructions.first().isJsonObject) {
                val instructionsObj = instructions.first().asJsonObject
                if (instructionsObj.get("@type").toString().contains("HowToStep", ignoreCase = true)) {
                    instructions.forEach {
                        ins.add(it.asJsonObject.get("text").toString())
                    }
                } else if (instructionsObj.get("@type").toString()
                        .contains("HowToSection", ignoreCase = true)
                ) {
                    instructionsObj.get("itemListElement").asJsonArray.forEach {
                        ins.add(it.asJsonObject.get("text").toString())
                    }
                }
            } else if (instructions.first().isJsonPrimitive) {
                instructions.forEach {
                    ins.add(it.toString())
                }
            }

        } else if (instructionsNode.isJsonObject) {
            val instructionsObj = instructionsNode.asJsonObject
            instructionsObj.get("itemListElement").asJsonArray.forEach {
                if (it.isJsonObject) {
                    val instructionObj = it.asJsonObject
                    val instruction = instructionObj.get("itemListElement").asJsonObject.get("text").toString()
                    ins.add(instruction)
                } else {
                    ins.add(it.toString())
                }
            }

        } else {
            ins.add("Något gick snett :(")
        }

        // clean the instructions from possible html tags and quotes before returning
        return ins.map {
            cleanString(Jsoup.parse(it).text().replace("""\n""", "\n"))
        }.distinct()
    }

    private fun traverseInstructions(document: Document, ingredients: List<String>): List<String> {
        val instructionMap = mutableMapOf<Node, Double>()

        document.traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                val nodeScore: Pair<Boolean, Double> = ScoreInstruction().isInstruction(node)
                if (nodeScore.first) {
                    instructionMap.put(node, nodeScore.second)
                }
            }

            override fun tail(node: Node?, depth: Int) {
                // Leave empty
            }
        })
        val (instructionNode1, instructionNode2) = findTwoUniqueEntries(instructionMap)
        val instructionLcaNode = lowestCommonAncestor(instructionNode1, instructionNode2)

        return instructionListFromNode(instructionLcaNode, ingredients)
    }


    private fun instructionListFromNode(inputNode: Node, ingredientList: List<String>): List<String> {
        /**
         * Returns a Nodes childnodes content as a list.
         * Different method compared to [ingredientListFromNode].
         * This one is suitable for instructions.
         */
        val list = mutableListOf<String>()
        inputNode.traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                val doc = Jsoup.parse(node.outerHtml())
                val text = cleanString( doc.body().text().trim() )
                println(text)
                if (
                    text.isNotEmpty() &&
                    text.split(" ").size > 2 &&
                    text !in list &&
                    !isPartOfIngredients(text, ingredientList)
                )
                    list.add(text)
            }

            override fun tail(node: Node, depth: Int) {
                // leave empty
            }
        })

        return list.distinct()
    }

    private fun isPartOfIngredients(instruction: String, ingredients: List<String>): Boolean {
        /**
         * Returns if an instruction contains a part match from the ingredient list.
         */
        ingredients.forEach { ingredient ->
            if (instruction in ingredient || ingredient in instruction) {
                return true
            }
        }
        return false
    }
}