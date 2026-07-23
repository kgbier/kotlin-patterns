package dev.kgbier.patterns.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import dev.kgbier.patterns.sample.repo.SettingsRepo
import dev.kgbier.patterns.sample.screen.dashboard.DashboardScreen
import dev.kgbier.patterns.sample.screen.settings.SettingsScreen

enum class Route {
    Menu,
    Settings,
    Dashboard,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.enableEdgeToEdge(window)

        setContent {
            var route: Route by remember { mutableStateOf(Route.Menu) }

            BackHandler(route != Route.Menu) { route = Route.Menu }

            Scaffold { contentPadding ->
                when (route) {
                    Route.Menu -> MainScreen(
                        navigate = { route = it },
                        modifier = Modifier
                            .padding(contentPadding)
                            .consumeWindowInsets(contentPadding)
                    )

                    Route.Settings -> SettingsScreen(
                        settingsRepo = remember { SettingsRepo() },
                        onBackPressed = { route = Route.Menu },
                        modifier = Modifier
                            .padding(contentPadding)
                            .consumeWindowInsets(contentPadding)
                    )

                    Route.Dashboard -> DashboardScreen(
                        onBackPressed = { route = Route.Menu },
                        modifier = Modifier
                            .padding(contentPadding)
                            .consumeWindowInsets(contentPadding)
                    )
                }
            }
        }
    }
}
