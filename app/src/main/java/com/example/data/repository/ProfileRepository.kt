package com.example.data.repository

import android.net.Uri
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Reads and writes each user's business profile in Firestore, and uploads
 * the company logo to Firebase Storage. Every document lives under the
 * signed-in user's own uid, so one account never sees another's setup.
 */
class ProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    private fun profileDoc(uid: String) = firestore.collection("users").document(uid)

    suspend fun getProfile(uid: String): UserProfile? {
        val snapshot = profileDoc(uid).get().await()
        return if (snapshot.exists()) snapshot.toObject(UserProfile::class.java) else null
    }

    suspend fun saveProfile(profile: UserProfile): Result<Unit> {
        return try {
            profileDoc(profile.uid)
                .set(profile.copy(updatedAt = System.currentTimeMillis()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Uploads the picked logo image and returns its public download URL. */
    suspend fun uploadLogo(uid: String, imageUri: Uri): Result<String> {
        return try {
            val ref = storage.reference.child("logos/$uid.jpg")
            ref.putFile(imageUri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
