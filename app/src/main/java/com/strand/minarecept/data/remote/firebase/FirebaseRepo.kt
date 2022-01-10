package com.strand.minarecept.data.remote.firebase

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.strand.minarecept.data.local.FirebaseRecipe
import com.strand.minarecept.util.ComparablePair
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

sealed class RecipesResponse<out T> {
    data class OnSuccess<T>(val querySnapshot: T?) : RecipesResponse<T>()
    data class OnError<E>(val exception: E?) : RecipesResponse<Nothing>()
}

class FirebaseRepo @Inject constructor() {

    private val firestoreDb = Firebase.firestore
    private val userId = Firebase.auth.currentUser?.uid

    @ExperimentalCoroutinesApi
    fun getAllRecipesFromFirebase() = callbackFlow {
        val collection = userId?.let {
            firestoreDb
                .collection("recipes")
                .document(it)
                .collection("allRecipes")
        }
        val snapshotListener = collection?.addSnapshotListener { value, error ->
            val response = if (error == null) {
                RecipesResponse.OnSuccess(value)
            } else {
                RecipesResponse.OnError(error)
            }

            this.trySend(response).isSuccess

            val source = if (value?.metadata?.isFromCache == true)
                "local cache"
            else
                "server"
            Log.d(TAG, "Data fetched from $source")
        }

        awaitClose {
            snapshotListener?.remove()
        }
    }

    /**
     * This extension function will rearrange the list,
     * placing Recipes that are in the customSortOrder at the front of the list.
     */
     fun List<FirebaseRecipe>.applySort(customSortOrder: List<String>): List<FirebaseRecipe> {
        return sortedBy { recipe ->
            val positionForItem = customSortOrder.indexOf(recipe.recipeId).let { order ->
                if (order > -1) order else Int.MAX_VALUE
            }
            ComparablePair(positionForItem, recipe.title)
        }
    }

    @ExperimentalCoroutinesApi
    fun loadCurrentRecipeFromFirebase(id: String) = callbackFlow {
        val docRef = userId?.let {
            firestoreDb
                .collection("recipes")
                .document(it)
                .collection("allRecipes")
                .document(id)
        }
        val snapshotListener = docRef?.addSnapshotListener { value, error ->
            val response = if (error == null) {
                RecipesResponse.OnSuccess(value)
            } else {
                RecipesResponse.OnError(error)
            }

            this.trySend(response).isSuccess
        }
        awaitClose {
            snapshotListener?.remove()
        }
    }

    fun addRecipeToFirebase(recipe: FirebaseRecipe, id: String) {
        if (userId != null) {
            firestoreDb
                .collection("recipes")      // root
                .document(userId)                       // userId
                .collection("allRecipes")   // allRecipes by this user
                .document(id)                           // recipeId
                .set(recipe)                            // the actual recipe
                .addOnSuccessListener { Log.d("Firestore", "Recipe added to db") }
                .addOnFailureListener { Log.d("Firestore", "Failed to add recipe") }
        } else {
            Log.d("Firestore", "Failed to add recipe due to auth error")
        }

    }

    fun updateRecipeInFirebase(recipe: FirebaseRecipe, id: String) {
        if (userId != null) {
            firestoreDb
                .collection("recipes")
                .document(userId)
                .collection("allRecipes")
                .document(id)
                .set(recipe, SetOptions.merge())
                .addOnSuccessListener { Log.d("Firestore", "Recipe updated in db") }
                .addOnFailureListener { Log.d("Firestore", "Failed to update recipe") }
        } else {
            Log.d("Firestore", "Failed to update recipe due to auth error")
        }
    }

    fun <T> updateRecipeField(doc: String, key: String, value: T) {
        val docRef = userId?.let {
            firestoreDb
                .collection("recipes")
                .document(it)
                .collection("allRecipes")
                .document(doc)
        }
        docRef
            ?.update(key, value)
            ?.addOnSuccessListener { Log.d(TAG, "Update successful") }
            ?.addOnFailureListener { e -> Log.w(TAG, "Error updating", e) }
    }

    fun deleteRecipe(doc: String) {
        // TODO: If a user deletes a recipe then I have to check if this recipeId is present
        //  in any of the user defined collections and delete it from there as well
        if (userId != null) {
            firestoreDb
                .collection("recipes")
                .document(userId)
                .collection("allRecipes")
                .document(doc)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Recipe successfully deleted") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting recipe", e) }
        } else {
            Log.d("Firestore", "Error deleting recipe due to auth error")
        }
    }

}

private const val TAG = "FirebaseRepo - Firestore"