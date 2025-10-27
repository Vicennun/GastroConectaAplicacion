package com.example.gastroconectaaplicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

// 1. Importa tu navegación
import com.example.gastroconectaaplicacion.ui.navigation.AppNavigation
// 2. Importa tu tema (este archivo ya existía)
import com.example.gastroconectaapp.ui.theme.GastroConectaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 3. Carga tu tema
            GastroConectaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 4. ¡Carga el controlador de navegación!
                    AppNavigation()
                }
            }
        }
    }
}