package com.oolestudio.tamashi

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.oolestudio.tamashi.data.*
import com.oolestudio.tamashi.ui.screens.LoginScreen
import com.oolestudio.tamashi.ui.screens.MainScreen
import com.oolestudio.tamashi.ui.screens.RegisterScreen
import com.oolestudio.tamashi.viewmodel.AuthViewModel
import com.oolestudio.tamashi.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview

// Clase sellada (sealed class) para representar las diferentes pantallas de navegación.
// Una sealed class restringe la jerarquía de clases a un conjunto definido,
// lo que permite un manejo exhaustivo y seguro de los tipos en estructuras 'when'.
sealed class Screen {
    object Login : Screen()    // 'object' define una instancia única (Singleton) para esta pantalla.
    object Register : Screen()
}

// --- Implementaciones Falsas (Mocks) para Previsualización ---
// Estas clases simulan el comportamiento de la base de datos y autenticación.
// Permiten visualizar la interfaz de usuario (UI) en el editor sin conectar a Firebase real.

class FakeAuthRepository : AuthRepository {
    // MutableStateFlow es un contenedor de estado observable.
    // Mantiene un valor actual y emite actualizaciones a los recolectores cuando cambia.
    private val authState = MutableStateFlow<UserState>(UserState.LoggedOut)

    // Expone el flujo de estados como solo lectura (Flow) para que la UI pueda observarlo.
    override fun getAuthState(): Flow<UserState> = authState

    // 'suspend' marca la función como asíncrona.
    // Estas funciones pueden suspender su ejecución sin bloquear el hilo principal,
    // ideal para operaciones de red o base de datos.
    override suspend fun registerUser(email: String, password: String, username: String): Result<Unit> {
        // Simulamos un registro exitoso actualizando el estado localmente.
        authState.value = UserState.LoggedIn("fake_user_id", listOf("password"))
        return Result.success(Unit) // Unit representa la ausencia de un valor de retorno significativo.
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        authState.value = UserState.LoggedIn("fake_user_id", listOf("password"))
        return Result.success(Unit)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        authState.value = UserState.LoggedIn("fake_google_user_id", listOf("google.com"))
        return Result.success(Unit)
    }

    override suspend fun logout() { authState.value = UserState.LoggedOut }
    override suspend fun reauthenticate(password: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateUsername(newUsername: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateUserEmail(newEmail: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateUserPassword(newPassword: String): Result<Unit> = Result.success(Unit)
    override suspend fun deleteUser(): Result<Unit> {
        authState.value = UserState.LoggedOut
        return Result.success(Unit)
    }
}

class FakePlaylistRepository : PlaylistRepository {
    private val playlists = MutableStateFlow<List<Playlist>>(emptyList())
    private val objectives = MutableStateFlow<List<Objective>>(emptyList())

    override suspend fun createPlaylist(name: String, category: String, colorHex: String): Playlist? {
        val newPlaylist = Playlist(id = (playlists.value.size + 1).toString(), name = name, category = category, colorHex = colorHex)
        // Actualizamos la lista creando una nueva copia con el elemento agregado (inmutabilidad).
        playlists.value = playlists.value + newPlaylist
        return newPlaylist
    }

    override fun getUserPlaylists(): Flow<List<Playlist>> = playlists

    override suspend fun deletePlaylist(playlistId: String): Result<Unit> {
        playlists.value = playlists.value.filterNot { it.id == playlistId }
        return Result.success(Unit)
    }

    override fun getObjectivesForPlaylist(playlistId: String): Flow<List<Objective>> = objectives

    override suspend fun addObjectiveToPlaylist(playlistId: String, name: String, description: String): Result<Unit> {
        val newObjective = Objective(id = (objectives.value.size + 1).toString(), name = name, description = description)
        objectives.value = objectives.value + newObjective
        return Result.success(Unit)
    }

    override suspend fun updateObjective(playlistId: String, objectiveId: String, name: String, description: String): Result<Unit> {
        // .map transforma cada elemento de la lista según la condición dada.
        objectives.value = objectives.value.map {
            // .copy() crea una nueva instancia del objeto (data class) modificando solo las propiedades especificadas.
            if (it.id == objectiveId) it.copy(name = name, description = description) else it
        }
        return Result.success(Unit)
    }

    override suspend fun updateObjectiveStatus(playlistId: String, objectiveId: String, isCompleted: Boolean): Result<Unit> {
        objectives.value = objectives.value.map {
            if (it.id == objectiveId) it.copy(completed = isCompleted) else it
        }
        return Result.success(Unit)
    }

    override suspend fun deleteObjective(playlistId: String, objectiveId: String): Result<Unit> {
        objectives.value = objectives.value.filterNot { it.id == objectiveId }
        return Result.success(Unit)
    }
}


// @Composable define una función que describe parte de la interfaz de usuario.
// Compose transforma estas funciones en elementos visuales.
// @Preview permite visualizar este componente en la herramienta de diseño del IDE sin ejecutar la app.
@Composable
@Preview
fun AppPreview() {
    App(
        authViewModel = AuthViewModel(FakeAuthRepository()),
        homeViewModel = HomeViewModel(FakePlaylistRepository())
    )
}

@Composable
fun App(authViewModel: AuthViewModel, homeViewModel: HomeViewModel) {
    // MaterialTheme aplica los principios de diseño Material (colores, tipografía, formas) a los componentes hijos.
    MaterialTheme {
        // collectAsState(): Observa el flujo de datos (Flow) y lo convierte en un Estado de Compose.
        // Cuando el flujo emite un nuevo valor, la UI se recompondrá (se actualizará) automáticamente.
        val authState by authViewModel.authState.collectAsState()
        
        // remember { mutableStateOf(...) }: Crea y recuerda un estado mutable interno del componente.
        // 'remember' asegura que el valor se mantenga entre recomposiciones (actualizaciones de la vista).
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

        // 'when' es una estructura de control de flujo que evalúa el estado.
        // Al usar una sealed class, el compilador verifica que se cubran todos los casos posibles.
        when (authState) {
            is UserState.LoggedIn -> {
                // Si el usuario ha iniciado sesión, mostramos la pantalla principal.
                MainScreen(authViewModel = authViewModel, homeViewModel = homeViewModel)
            }
            is UserState.LoggedOut -> {
                // Si no hay sesión, gestionamos la navegación entre Login y Registro.
                when (currentScreen) {
                    is Screen.Login -> LoginScreen(
                        viewModel = authViewModel,
                        // Pasamos una función lambda para manejar la navegación al hacer clic en registrarse.
                        onNavigateToRegister = { currentScreen = Screen.Register }
                    )
                    is Screen.Register -> RegisterScreen(
                        viewModel = authViewModel,
                        onNavigateToLogin = { currentScreen = Screen.Login }
                    )
                }
            }
        }
    }
}

// Sobrecarga de la función App para facilitar su uso cuando solo se dispone del AuthViewModel.
@Composable
fun App(authViewModel: AuthViewModel) {
    App(authViewModel, HomeViewModel(FakePlaylistRepository()))
}