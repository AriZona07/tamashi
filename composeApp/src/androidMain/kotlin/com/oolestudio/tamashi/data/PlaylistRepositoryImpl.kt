package com.oolestudio.tamashi.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PlaylistRepositoryImpl : PlaylistRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val currentUser get() = auth.currentUser ?: throw IllegalStateException("Usuario no autenticado.")
    private val playlistsCollection get() = db.collection("users").document(currentUser.uid).collection("playlists")

    override suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist? {
        return try {
            val newPlaylistData = hashMapOf("name" to name, "category" to category, "colorHex" to colorHex)
            val documentReference = playlistsCollection.add(newPlaylistData).await()
            Playlist(id = documentReference.id, name = name, category = category, colorHex = colorHex)
        } catch (e: Exception) { null }
    }

    override fun getUserPlaylists(): Flow<List<Playlist>> = callbackFlow {
        val listener = playlistsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            if (snapshot != null) {
                val playlists = snapshot.documents.mapNotNull { doc -> doc.toObject<Playlist>()?.copy(id = doc.id) }
                trySend(playlists)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun deletePlaylist(playlistId: String): Result<Unit> {
        return try {
            playlistsCollection.document(playlistId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override fun getObjectivesForPlaylist(playlistId: String): Flow<List<Objective>> = callbackFlow {
        val listener = playlistsCollection.document(playlistId).collection("objectives")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) {
                    val objectives = snapshot.documents.mapNotNull { doc -> doc.toObject<Objective>()?.copy(id = doc.id) }
                    trySend(objectives)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addObjectiveToPlaylist(playlistId: String, name: String, description: String): Result<Unit> {
        return try {
            val newObjective = hashMapOf(
                "name" to name,
                "description" to description,
                "completed" to false // Nombre de campo correcto
            )
            playlistsCollection.document(playlistId).collection("objectives").add(newObjective).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateObjective(playlistId: String, objectiveId: String, name: String, description: String): Result<Unit> {
        return try {
            val updatedData = mapOf("name" to name, "description" to description)
            playlistsCollection.document(playlistId).collection("objectives").document(objectiveId).update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun updateObjectiveStatus(playlistId: String, objectiveId: String, isCompleted: Boolean): Result<Unit> {
        return try {
            playlistsCollection.document(playlistId).collection("objectives").document(objectiveId)
                .update("completed", isCompleted).await() // Nombre de campo correcto
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun deleteObjective(playlistId: String, objectiveId: String): Result<Unit> {
        return try {
            playlistsCollection.document(playlistId).collection("objectives").document(objectiveId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}