package com.example.distribuidora_de_huevos_jd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.distribuidora_de_huevos_jd.navigation.NavegacionApp
import com.example.distribuidora_de_huevos_jd.ui.theme.DISTRIBUIDORA_DE_HUEVOSJDTheme

/** Punto de entrada de la app: aplica el tema y abre la navegación. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DISTRIBUIDORA_DE_HUEVOSJDTheme {
                NavegacionApp()
            }
        }
    }
}