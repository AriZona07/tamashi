package com.oolestudio.tamashi.data

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.oolestudio.tamashi.R

// This class is now internal to the Android implementation
private class GoogleAuthHandlerAndroid(private val context: android.content.Context) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val signInIntent: Intent = googleSignInClient.signInIntent

    fun handleSignInResult(result: androidx.activity.result.ActivityResult, onSuccess: (String) -> Unit, onError: () -> Unit) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                onSuccess(idToken)
            } catch (e: ApiException) {
                onError()
            }
        } else {
            onError()
        }
    }
}

// This is the actual implementation of the expect function
@Composable
actual fun rememberGoogleAuthHandler(onTokenReceived: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val handler = remember { GoogleAuthHandlerAndroid(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handler.handleSignInResult(
            result = result,
            onSuccess = onTokenReceived,
            onError = { /* TODO: Handle error, e.g., show a toast */ }
        )
    }

    return {
        launcher.launch(handler.signInIntent)
    }
}

// We don't need the expect class anymore
actual class GoogleAuthHandler {
    // This is not used
}
