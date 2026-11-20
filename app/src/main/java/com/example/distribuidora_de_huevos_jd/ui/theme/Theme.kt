package com.example.distribuidora_de_huevos_jd.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = VerdeAzul,
    onPrimary = Color.White,
    primaryContainer = AmarilloClaro,
    onPrimaryContainer = VerdeAzulOscuro,
    secondary = HuevoOscuro,
    onSecondary = CafeTexto,
    secondaryContainer = AmarilloClaro,
    onSecondaryContainer = CafeTexto,
    tertiary = Huevo,
    onTertiary = CafeTexto,
    background = FondoClaro,
    surface = FondoClaro
)

private val DarkColorScheme = darkColorScheme(
    primary = VerdeAzulClaro,
    onPrimary = VerdeAzulOscuro,
    primaryContainer = VerdeAzulOscuro,
    onPrimaryContainer = AmarilloClaro,
    secondary = Huevo,
    onSecondary = CafeTexto,
    secondaryContainer = VerdeAzulOscuro,
    onSecondaryContainer = AmarilloClaro,
    tertiary = HuevoOscuro,
    onTertiary = CafeTexto,
    background = FondoOscuro,
    surface = SuperficieOscura
)

@Composable
fun DISTRIBUIDORA_DE_HUEVOSJDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // En false para que siempre se usen los colores de la marca
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}