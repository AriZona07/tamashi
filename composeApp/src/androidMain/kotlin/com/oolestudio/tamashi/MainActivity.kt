package com.oolestudio.tamashi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.oolestudio.tamashi.data.AuthRepositoryImpl
import com.oolestudio.tamashi.data.PlaylistRepositoryImpl
import com.oolestudio.tamashi.viewmodel.AuthViewModel
import com.oolestudio.tamashi.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    // Instanciamos las implementaciones reales de los repositorios para Android
    private val authRepository by lazy { AuthRepositoryImpl() }
    private val playlistRepository by lazy { PlaylistRepositoryImpl() }

    // Creamos los ViewModels y les pasamos los repositorios reales
    private val authViewModel by lazy { AuthViewModel(authRepository) }
    private val homeViewModel by lazy { HomeViewModel(playlistRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inicializamos Firebase
        Firebase.initialize(context = this)

        setContent {
            // Pasamos los ViewModels reales a nuestra aplicación de Compose
            App(authViewModel, homeViewModel)
        }
    }
}