package com.example.minarecept.data.remote.parsing

import android.util.Log
import com.example.minarecept.data.remote.ScoreIngredient
import com.example.minarecept.util.cleanString
import com.google.gson.JsonObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.select.NodeVisitor

class ParseIngredients {

    fun getIngredients(
        jsonLd: JsonObject,
        document: Document
    ): List<String>? {
        return try {
            jsonIngredients(jsonLd)
        } catch (e: Exception) {
            Log.d("Ingredients: jsonld", e.message.toString())

            try {
                traverseIngredients(document)
            } catch (e: Exception) {
                Log.d("Ingredients: traverse", e.message.toString())
                null
            }
        }
    }

    private fun jsonIngredients(obj: JsonObject): List<String> {
        return obj.get("recipeIngredient").asJsonArray.map {
            cleanString(it.toString())
        }.distinct()
    }

    private fun traverseIngredients(document: Document): List<String> {
        val ingredientMap = mutableMapOf<Node, Double>()

        document.traverse(object : NodeVisitor {
            override fun head(node: Node, depth: Int) {
                val nodeScore: Pair<Boolean, Double> = ScoreIngredient().isIngredient(node)
                if (nodeScore.first) {
                    ingredientMap.put(node, nodeScore.second)
                }
            }

            override fun tail(node: Node?, depth: Int) {
                // Leave empty
            }
        })
        val (ingredientNode1, ingredientNode2) = findTwoUniqueEntries(ingredientMap)
        val ingredientLcaNode = lowestCommonAncestor(ingredientNode1, ingredientNode2)

        return ingredientListFromNode(ingredientLcaNode)
    }


    private fun ingredientListFromNode(node: Node): List<String> {
        /**
         * Return a Nodes childnodes content as a list.
         * Different method compared to [instructionListFromNode].
         * This one is suitable for ingredients.
         */
        return node.childNodes()
            .map { cleanString( Jsoup.parse(it.outerHtml()).body().text() ) }
            .filter { it.isNotEmpty() }
    }
}