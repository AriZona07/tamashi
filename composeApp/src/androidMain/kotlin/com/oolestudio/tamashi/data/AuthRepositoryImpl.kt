package com.oolestudio.tamashi.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun getAuthState(): Flow<UserState> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(UserState.LoggedOut)
            } else {
                val providerIds = firebaseUser.providerData.map { it.providerId }
                trySend(UserState.LoggedIn(firebaseUser.uid, providerIds))
            }
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun reauthenticate(password: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Usuario no autenticado."))
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun registerUser(email: String, password: String, username: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val user = hashMapOf("name" to username, "email" to email)
                db.collection("users").document(firebaseUser.uid).set(user).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error desconocido al crear el usuario."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            
            val isNewUser = result.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = hashMapOf(
                        "username" to firebaseUser.displayName,
                        "email" to firebaseUser.email,
                        "xp" to 0,
                        "level" to 1
                    )
                    db.collection("users").document(firebaseUser.uid).set(user).await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateUsername(newUsername: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Usuario no autenticado."))
            db.collection("users").document(user.uid).update("username", newUsername).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserEmail(newEmail: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Usuario no autenticado."))
            user.updateEmail(newEmail).await() // <-- CORRECCIÓN AQUÍ
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserPassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Usuario no autenticado."))
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Usuario no autenticado."))
            db.collection("users").document(user.uid).delete().await()
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}